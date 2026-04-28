def connack(reason_code: int):

    return bytearray([0x20, 0x02, 0x01, reason_code])

"""
    0x20 = MQTT Control Packet Type (Fixed Header)
    0x02 = Remaining Length (FACE PARTE TOT DIN FIXED HEADER, lungimea al variable header)
    0x01 = CONNACK flag (0x01 = Client conectat)
    0x00 = Connect Reason Code (0x00 inseamna ca conexiunea a avut succes)
    0x86 = Bad User Name or Password
"""


def disconnect() -> bytearray:
    return bytearray([0xE0, 0x01, 0x04])
"""
    0xE0 = Disconnect Control Packet Type (Fixed Header)
    0x01 = Remaining Length (pentru Reason Code)
    0x04 = Disconnect Reason Code (0x04 = Client disconnected with Will Message)
"""

def pingresp() -> bytearray:
    return bytearray([0xD0, 0x00])
"""
    0xD0 = pingresp control packet type
    0x00 = lungimea ramasa (adica nu are nici variable header, nici payload)

"""


def puback(packet_id: int):
    # packet_id este un numar pe 16 biti (0-65535) care identifica unic mesajul.
    fixed_header = 0x40  # 0x40 este pentru PUBACK
    remaining_length = 0x02  # lungimea ramasa la variable header
    msb = (packet_id >> 8) & 0xFF  # Octetul cel mai mult semnificativ
    lsb = packet_id & 0xFF  # Octetul cel mai putin semnificativ
    return bytearray([fixed_header, remaining_length, msb, lsb])

def suback(packet_id, qos_levels):
    """
    Creează un pachet SUBACK pentru a răspunde la un pachet SUBSCRIBE.

    packet_id: ID-ul pachetului SUBSCRIBE primit.
    qos_levels: O listă de niveluri QoS corespunzătoare fiecărui topic.
    returns: Bytearray reprezentând pachetul SUBACK.
    """
    fixed_header = 0x90
    variable_header = packet_id.to_bytes(2, 'big')  # MSB și LSB ale Packet ID
    payload = bytes(qos_levels)  # Nivelurile QoS ca secvență de octeți
    remaining_length = len(variable_header) + len(payload)  # Lungimea variabilă + payload

    # Construcția pachetului SUBACK
    return bytearray([fixed_header, remaining_length]) + variable_header + payload


def pubrec(packet_id) -> bytearray:
    fixed_header = 0x50
    remaining_length = 0x02
    msb = (packet_id >> 8) & 0xFF
    lsb = packet_id & 0xFF

    return bytearray([fixed_header, remaining_length, msb, lsb])


def pubcomp(packet_id) -> bytearray:
    fixed_header = 0x70
    remaining_length = 0x02
    msb = (packet_id >> 8) & 0xFF
    lsb = packet_id & 0xFF
    return bytearray([fixed_header, remaining_length, msb, lsb])

def create_subscribe_packet(packet_id, subscriptions):
    """Create a SUBSCRIBE packet."""
    fixed_header = 0x82  # SUBSCRIBE packet type and flags
    variable_header = packet_id.to_bytes(2, 'big')  # Packet ID
    payload = bytearray()

    for topic, qos in subscriptions:
        topic_bytes = topic.encode('utf-8')
        topic_length = len(topic_bytes).to_bytes(2, 'big')
        payload.extend(topic_length)
        payload.extend(topic_bytes)
        payload.append(qos)

    remaining_length = len(variable_header) + len(payload)
    fixed_header_bytes = bytearray([fixed_header, remaining_length])

    return fixed_header_bytes + variable_header + payload