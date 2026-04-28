import packets as pk
class PacketHandler:

    @staticmethod
    def handle_connect(packet):
        packet_bytes = bytearray(packet)

        # extrage valoarea keep-alive
        keep_alive = int.from_bytes(packet_bytes[10:12], byteorder='big')

        # lungimea username-ului se afla la octetul 37
        user_length = packet_bytes[37]

        # lungimea parolei se afla dupa username, la octetul corespunzator
        pass_length = packet_bytes[37 + user_length + 2]

        # definirea intervalului in care se afla username-ul
        user_start_idx = 38  # prima litera a username-ului
        user_end_idx = user_start_idx + user_length
        username = packet_bytes[user_start_idx:user_end_idx].decode("utf-8")

        # definirea intervalului in care se afla parola
        pass_start_index = user_end_idx + 2
        pass_end_index = pass_start_index + pass_length
        password = packet_bytes[pass_start_index:pass_end_index].decode("utf-8")

        # punem mesajul de Last Will
        will_message = "Client disconnected unexpectedly. This is the Last Will message."

        # returneaza username, parola,  valoarea keep-alive, mesaj last will si topic
        return username, password, keep_alive, will_message



    @staticmethod
    def parse_publish_packet(data):
        # extrag lungimea topicului
        topic_len = int.from_bytes(data[2:4], 'big')

        # topicul se afla incepand cu octetul 4
        topic = data[4:4 + topic_len].decode()

        # valoarea QoS se afla in octetul 1, incepand cu bitul 1
        qos = (data[0] >> 1) & 0x03

        # indexul de start al payload-ului este 4 + lungimea topicului
        payload_start = 4 + topic_len

        # daca QoS > 0, extragem packet_id-ul
        if qos > 0:
            packet_id = int.from_bytes(data[payload_start:payload_start + 2], 'big')
            payload_start += 2
        else:
            packet_id = None

        # extragem payload-ul
        payload = data[payload_start:]

        try:
            # decodeaza payload-ul in utf-8
            payload_text = payload.decode('utf-8')
        except UnicodeDecodeError:
            # trimitem payload-ul ca si bytes daca nu poate fi decodat
            payload_text = repr(payload)

        return topic, qos, payload_text, packet_id

    @staticmethod
    def extract_packet_id(packet: bytes) -> int:
        """
        extrage packet identifier dintr-un pachet subscribe mqtt.

        args:
            packet (bytes): pachetul subscribe mqtt.

        returns:
            int: identificatorul de pachet extras.
        """
        # verifica daca pachetul este subscribe
        if len(packet) < 4 or (packet[0] >> 4) != 0x08:
            raise ValueError("Packet of invalid type or not SUBSCRIBE.")

        # extrage packet identifier din bytes 2 si 3 si converteste la int
        packet_id = int.from_bytes(packet[2:4], byteorder='big')
        return packet_id

    # mai degraba gestioneaza nivelele de QoS si trimite confirmari de livrare
    @staticmethod
    def handle_publish_packet(client_socket, data):
        """
        gestioneaza pachetul publish primit de la client.

        args:
            client_socket (socket): socket-ul clientului.
            data (bytes): pachetul primit de la client.

        """
        # parseaza datele din pachetul publish
        topic, qos, payload_text, packet_id = PacketHandler.parse_publish_packet(data)

        # afiseaza informatiile primite
        print(f"For topic: {topic} the Message received is: {payload_text}")
        print(f"QoS received: {qos}")
        print(f"Packet ID: {packet_id}")


        # gestioneaza nivelul qos
        if qos == 0:
            # qos 0 - livrare fara confirmare
            print("Message delivered with QoS 0.")
            print()

        elif qos == 1:
            # qos 1 - confirmare necesara
            client_socket.send(pk.puback(packet_id))
            print(f"PUBACK sent for Packet ID {packet_id}.")
            print()

        elif qos == 2:
            # qos 2 - livrare cu confirmare avansata
            client_socket.send(pk.pubrec(packet_id)) # publish received
            print(f"PUBREC sent for Packet ID {packet_id}.")

            data_pubrel = client_socket.recv(1024) # verifies if the packet is PUBREL - release

            if PacketHandler.is_pubrel(data_pubrel, packet_id):
                client_socket.send(pk.pubcomp(packet_id))
                print(f"PUBCOMP sent for Packet ID {packet_id}.") # publish complete
                print()
            else:
                print("PUBREL invalid or missing.")


    @staticmethod
    def handle_subscribe_packet(data):
        try:
            # Verify that the packet is long enough
            if len(data) < 6:
                raise ValueError("Invalid SUBSCRIBE packet length")

            # Extract the packet identifier
            packet_id = int.from_bytes(data[2:4], 'big')

            # Initialize lists to store topics and QoS levels
            topics = []
            qos_levels = []

            # Start parsing topics and QoS levels from byte 4
            index = 4
            while index < len(data):
                # Extract topic length
                topic_len = int.from_bytes(data[index:index + 2], 'big')
                index += 2

                # Extract topic
                topic = data[index:index + topic_len]
                try:
                    topic = topic.decode('utf-8')
                except UnicodeDecodeError as e:
                    print(f"Invalid UTF-8 encoding in topic at index {index}: {e}")
                    # Skip the invalid part and continue parsing
                    index += topic_len
                    continue

                index += topic_len

                # Extract QoS level
                if index >= len(data):
                    raise ValueError("Missing QoS level for topic")
                qos = data[index]
                index += 1

                # Append topic and QoS level to the lists
                topics.append(topic)
                qos_levels.append(qos)

            return packet_id, topics, qos_levels

        except Exception as e:
            print(f"Error handling SUBSCRIBE packet: {e}")
            return None, None, None

    @staticmethod
    def process_subscribe_packets(data):
        """
        proceseaza unul sau mai multe pachete SUBSCRIBE dintr-un singur buffer.
        data: datele primite (un buffer cu unul sau mai multe pachete SUBSCRIBE).
        returneaza o lista de tuple
        """
        processed_subscriptions = []
        index = 0

        while index < len(data):
            try:
                # verifica daca pachetul are tipul SUBSCRIBE (0x82)
                if data[index] >> 4 != 8:
                    print(f"Invalid packet type at index {index}. Skipping.")
                    break

                # lungimea pachetului
                remaining_length = data[index + 1]
                packet_data = data[index:index + 2 + remaining_length]

                # decodifica pachetul SUBSCRIBE
                packet_id, topics, qos_levels = PacketHandler.handle_subscribe_packet(packet_data)

                for topic, qos in zip(topics, qos_levels):
                    if isinstance(topic, bytes):  # decodifica doar dacă este de tip bytes
                        try:
                            topic = topic.decode('utf-8')  # verifica validitatea UTF-8
                        except UnicodeDecodeError as e:
                            print(f"Invalid UTF-8 encoding in topic at index {index}: {e}")
                            continue  # next topic

                    processed_subscriptions.append((packet_id, topic, qos))

                # creste index pt urmatorul pachet
                index += 2 + remaining_length

            except Exception as e:
                print(f"Error processing SUBSCRIBE packet at index {index}: {e}")
                break

        return processed_subscriptions

    @staticmethod
    def is_pubrel(data, packet_id):
        # verifica daca pachetul este pubrel si are packet id corespunzator
        return (data[0] >> 4) == 6 and int.from_bytes(data[2:4], 'big') == packet_id
