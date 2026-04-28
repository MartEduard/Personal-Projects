# hardware.py
import machine
import dht
import ssd1306
import config

# initializare obiecte
try:
    i2c = machine.I2C(0, sda=machine.Pin(config.PIN_SDA), scl=machine.Pin(config.PIN_SCL), freq=400000)
    oled_disp = ssd1306.SSD1306_I2C(128, 32, i2c, addr=0x3c)
except:
    oled_disp = None

mq2 = machine.ADC(config.PIN_MQ2)
dht_sensor = dht.DHT22(machine.Pin(config.PIN_DHT))

buzzer = machine.Pin(config.PIN_BUZZER, machine.Pin.OUT)
releu = machine.Pin(config.PIN_RELEU, machine.Pin.OUT)
led_rosu = machine.Pin(config.PIN_LED_ROSU, machine.Pin.OUT)
led_galben = machine.Pin(config.PIN_LED_GALBEN, machine.Pin.OUT)
led_verde = machine.Pin(config.PIN_LED_VERDE, machine.Pin.OUT)

# functii senzori
def read_gaz():
    return mq2.read_u16()

def read_dht():
    # return tuple (temp, hum) sau None daca e eroare
    try:
        dht_sensor.measure()
        return dht_sensor.temperature(), dht_sensor.humidity()
    except:
        return None

# functii actuatori
def set_leds(mode):
    # Reset
    led_rosu.value(0); led_galben.value(0); led_verde.value(0)
    
    if mode == 'R': led_rosu.value(1)
    elif mode == 'G': led_galben.value(1)
    elif mode == 'V': led_verde.value(1)
    elif mode == 'OFF': pass

def toggle_led_galben():
    led_galben.toggle()

def set_fan(state_on):
    releu.value(1 if state_on else 0)

def set_buzzer(state_on):
    buzzer.value(1 if state_on else 0)

# functii de pe OLED
def update_oled(status, gaz, temp, humidity, trend_text):
    if not oled_disp: return
    oled_disp.fill(0)
    if "RACIRE" in status: 
        oled_disp.text("RACIRE...", 30, 0)
    else: 
        oled_disp.text(status[:16], 0, 0)
        
    oled_disp.text(f"T:{temp:.1f} G:{gaz}", 0, 15)
    oled_disp.text(f"H:{humidity:.0f}", 0, 25)
    oled_disp.text(f"Tr:{trend_text}", 35, 25)
    oled_disp.show()

def draw_calibration_bar(current_step, total_steps):
    if not oled_disp: return
    
    oled_disp.fill(0)
    
    # titlu
    oled_disp.text("CALIBRARE...", 20, 0)
    
    # bara
    bar_width = 100      # latimea totala a barei (pixeli)
    bar_height = 10      # inaltimea barei
    x_start = 14         # centrat orizontal: (128 - 100) / 2
    y_start = 16         # pozitia verticala
    
    # contur
    oled_disp.rect(x_start, y_start, bar_width, bar_height, 1)
    
    # calcul umplere
    # (pas_curent / total) * latime_maxima
    if total_steps > 0:
        fill_width = int((current_step / total_steps) * bar_width)
        
        # umplere dreptunghi
        oled_disp.fill_rect(x_start, y_start, fill_width, bar_height, 1)
    
    oled_disp.show()


