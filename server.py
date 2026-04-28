# server.py
import network
import uasyncio as asyncio
import json
import config
import interface

def connect_wifi():
    try:
        w = network.WLAN(network.AP_IF)
        w.config(essid=config.WIFI_SSID, password=config.WIFI_PASS)
        w.active(True)
        print(f'Server pornit la IP: {w.ifconfig()[0]}')
        return w.ifconfig()[0]
    except Exception as e:
        print("Eroare WiFi:", e)
        return None

async def handle_client(reader, writer, shared_state):
    try:
        request = await reader.read(1024)
        req_str = str(request)
        
        if 'GET /data' in req_str:
            # trimitem datele JSON (din dictionarul shared_state)
            header = 'HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n'
            await writer.awrite(header + json.dumps(shared_state))
        else:
            # trimitem pagina HTML
            header = 'HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n'
            await writer.awrite(header + interface.HTML_PAGE)
            
    except Exception as e:
        print("Eroare server:", e)
    finally:
        await writer.aclose()

async def start_server(shared_state):
    connect_wifi()
    # pornim serverul si ii pasam dictionarul de stare
    await asyncio.start_server(lambda r, w: handle_client(r, w, shared_state), config.SERVER_IP, config.SERVER_PORT)
