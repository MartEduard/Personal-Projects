# main.py
import uasyncio as asyncio
import time
import gc

# importam modulele noastre
import config
import hardware
import server

# variabile globale
state = {
    "gaz": 0, "temp": 0.0, "hum": 0.0,
    "status": "BOOT", "fan": "OFF",
    "dt": 0.0, "dh": 0.0
}

# referinte si flag-uri
PRAG_FUM = 0
PRAG_GAZ_CRITIC = 0
TEMP_REF = 0.0
HUM_REF = 0.0
prev_temp_read = 0.0
was_in_danger = False

# variabila pentru confirmarea racirii (histerezis)
cooling_counter = 0 
COOLING_THRESHOLD = 3 # e nevoie de 3 citiri consecutive de scadere ca sa schimbam starea

async def main_loop():
    global PRAG_FUM, PRAG_GAZ_CRITIC, TEMP_REF, HUM_REF, prev_temp_read, was_in_danger, cooling_counter

    # A. Init DHT (asteptam citire valida)
    while True:
        dht_data = hardware.read_dht()
        if dht_data:
            TEMP_REF, HUM_REF = dht_data
            prev_temp_read = TEMP_REF
            break
        await asyncio.sleep(1)

    # B. Calibrare Gaz
    # 200 pasi x 0.1s = 20 secunde
    s = 0
    total_steps = 200
    for i in range(total_steps):
        val = hardware.read_gaz()
        s += val
        
        # bara de loading
        hardware.draw_calibration_bar(i, total_steps)
        
        # animatie
        if i % 20 == 0: 
            print(f"Calibrare... {int(i/2)}%")
            hardware.toggle_led_galben() # galben itermitent la calibrare
            
        await asyncio.sleep(0.1)
    
    hardware.set_leds('OFF') # stingem dupa calibrare
    
    base_gaz = int(s / 200)
    PRAG_FUM = base_gaz + 2000
    PRAG_GAZ_CRITIC = base_gaz + 10000
    print(f"GATA! Baza: {base_gaz} | Fum: {PRAG_FUM} | Critic: {PRAG_GAZ_CRITIC}")

    # 2. START SERVER
    asyncio.create_task(server.start_server(state))
    print("Sistem ONLINE.")

    # 3. BUCLA PRINCIPALA
    curr_t = TEMP_REF
    curr_h = HUM_REF
    last_dht_read = 0
    
    while True:
        # citire senzori
        gaz = hardware.read_gaz()
        
        # sampling DHT
        now = time.ticks_ms()
        if time.ticks_diff(now, last_dht_read) > config.DHT_SAMPLE_INTERVAL:
            dht_val = hardware.read_dht()
            if dht_val:
                # salvam valoarea anterioara pentru comparatie
                # dar o salvam pe cea FILTRATA, nu pe cea raw, pentru stabilitate
                prev_temp_read = curr_t 
                
                t_raw, h_raw = dht_val
                
                # filtru EMA (Exponential Moving Average) usor modificat
                # punem mai multa greutate pe citirea noua (0.4) pentru reactie mai rapida
                curr_t = (curr_t * 0.6) + (t_raw * 0.4)
                curr_h = (curr_h * 0.6) + (h_raw * 0.4)
                
                # LOGICA DE TREND SI HISTEREZIS
                # verificam daca temperatura a scazut fata de tura trecuta
                if curr_t < (prev_temp_read - 0.05):
                    cooling_counter += 1 # confirmam racirea
                else:
                    cooling_counter = 0  # s-a incalzit sau a stagnat -> resetam contorul
                
                # limitare counter (sa nu creasca la infinit)
                if cooling_counter > 10: cooling_counter = 10
                
            last_dht_read = now

        # flag stabilizat: consideram ca e racire doar daca avem 3 confirmari
        is_confirmed_cooling = (cooling_counter >= COOLING_THRESHOLD)

        # calcule Delta
        delta_t = curr_t - TEMP_REF
        delta_h = HUM_REF - curr_h

        # actualizare state
        state.update({
            "gaz": gaz, "temp": curr_t, "hum": curr_h,
            "dt": delta_t, "dh": delta_h
        })

        # LOGICA DECIZIONALA
        status = "NORMAL"
        led_req = 'V'
        fan_req = False
        buzz_req = 0    # 0=off, 1=slow, 2=fast, 3=pulse

        # 1. RISC EXPLOZIE
        if gaz >= PRAG_GAZ_CRITIC:
            status = "PERICOL EXPLOZIE"
            led_req = 'R'; fan_req = True; buzz_req = 2
            was_in_danger = True
        
        # 2. TEMPERATURA RIDICATA (+10 grade)
        elif delta_t >= 10.0:
            
            # A. Daca NU avem confirmarea stabila ca se raceste -> ALARMA
            # (Chiar daca a scazut o data, ramanem pe rosu pana suntem siguri)
            if not is_confirmed_cooling:
                
                if gaz >= PRAG_FUM and delta_h > 0:
                    status = "INCENDIU"
                    led_req = 'R'; fan_req = True; buzz_req = 2
                    was_in_danger = True
                elif gaz >= PRAG_FUM:
                    status = "SUPRAINCALZIRE+FUM"
                    led_req = 'G'; fan_req = True; buzz_req = 1
                else:
                    status = "SUPRAINCALZIRE HW"
                    led_req = 'G'; fan_req = True; buzz_req = 1

            # B. Avem confirmarea ca se raceste (3 citiri la rand in scadere)
            else:
                if gaz >= PRAG_FUM:
                    status = "RACIRE - FUM (!)"
                    led_req = 'G_BLINK'; fan_req = True; buzz_req = 3 # Puls scurt
                else:
                    status = "RACIRE COMPONENTE"
                    led_req = 'G_BLINK'; fan_req = True; buzz_req = 0 # Silent

        # 3. FUM
        elif gaz >= PRAG_FUM:
            if was_in_danger:
                status = "VENTILARE FORTATA"
                led_req = 'R'; fan_req = True; buzz_req = 0
            else:
                status = "ATENTIE - FUM"
                led_req = 'G'; fan_req = True; buzz_req = 1

        # 4. NORMAL
        else:
            status = "AER CURAT"
            led_req = 'V'; fan_req = False; buzz_req = 0
            was_in_danger = False
            
            # Invatare lenta
            if abs(delta_t) < 0.5:
                TEMP_REF = (TEMP_REF * 0.98) + (curr_t * 0.02)
                HUM_REF = (HUM_REF * 0.98) + (curr_h * 0.02)

        # HARDWARE EXECUTE
        state['status'] = status
        state['fan'] = "ON" if fan_req else "OFF"
        
        hardware.set_fan(fan_req)
        
        if led_req == 'G_BLINK':
            hardware.set_leds('OFF')
            if int(time.ticks_ms()/250) % 2 == 0: hardware.toggle_led_galben()
        else:
            hardware.set_leds(led_req)

        # logica buzzer
        ts = int(time.ticks_ms())
        if buzz_req == 2: # Alarm
            hardware.set_buzzer(True); await asyncio.sleep(0.1); hardware.set_buzzer(False)
        elif buzz_req == 1: # Warn
            hardware.set_buzzer((ts // 1000) % 2 == 0)
            await asyncio.sleep(0.05)
        elif buzz_req == 3: # Pulse Scurt
            if (ts % 2000) < 200: hardware.set_buzzer(True)
            else: hardware.set_buzzer(False)
            await asyncio.sleep(0.05)
        else:
            hardware.set_buzzer(False)
            await asyncio.sleep(0.05)

        # OLED Logic
        trend_str = 'DOWN' if is_confirmed_cooling else 'UP/STABIL'
        if buzz_req == 3: trend_str = "DOWN+FUM"
        hardware.update_oled(status, gaz, curr_t, curr_h, trend_str)

        gc.collect()

# Rulare
try:
    asyncio.run(main_loop())
except KeyboardInterrupt:
    print("Oprit.")
    hardware.set_leds('OFF')
    hardware.set_fan(False)
    hardware.set_buzzer(False)


