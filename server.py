import socket
import threading
import time


from handler import PacketHandler
import packets as pk
from database import DB
from sessions import SessionManager
from session_logger import SessionLogger

class ServerMQTT:
    def __init__(self, ui, ip=socket.gethostbyname(socket.gethostname()), port=1883):
        self.lock = threading.Lock()
        self.ip_addr = ip
        print(ip)
        self.port = port
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((ip, port))
        self.server_socket.listen(20) # numarul maxim de conexiuni in asteptare
        self.status= False # status pentru conexiunea on/off intre client si server
        self.db = DB() # Baza de date pentru verificate user si parola
        self.clients = []
        self.keep_alive_value = 0
        self.handler = PacketHandler()
        self.keep_alive_timer = None  # flag timer pentru keep-alive
        self.keep_alive_timers = {} # dictionar pentru timeri de keep_alive pt fiecare client
        self.blocked_clients = {}  # dictionar pentru clientii blocati
        self.topic_history = [] # lista de topic-uri la care s-a abonat clientul
        self.topics = {} # dictionar pentru topic-uri si clientii abonati
        self.last_messages = {} # dictionar pentru ultimele 10 mesaje publicate
        self.qos_messages = [] # lista pentru mesajele QoS 1 si QoS 2
        self.last_keep_alive_time = {} # dictionar pentru timpul ultimului keep-alive
        self.session_manager = SessionManager()  # manager pentru sesiuni
        self.session_logger=SessionLogger() # logger pentru sesiuni
        self.stop_event = threading.Event()
        self.ui = ui # referinta la interfata grafica
        self.last_will_messages = {}  # Dictionary to store Last Will messages
        self.last_will_topic = "Last_Will_Topic/LWTopic"  # Default Last Will topic for all the clients
        self.add_topic(self.last_will_topic)  # Add the Last Will topic to the list of topics
        self.current_client_socket = None # Socket for the current client connection



    def reset_keep_alive_timer(self, client_socket, keep_alive, will_message=None):
        """Resets the keep-alive timer for the client connection."""
        try:
            client_address = client_socket.getpeername()
        except OSError:
            print("Socket already closed.")
            return

        self.last_keep_alive_time[client_address] = time.time()

        if client_address in self.keep_alive_timers:
            self.keep_alive_timers[client_address].cancel()
        self.keep_alive_timers[client_address] = threading.Timer(keep_alive, self.check_keep_alive_timeout, [client_socket, will_message])
        self.keep_alive_timers[client_address].start()

    def check_keep_alive_timeout(self, client_socket, will_message=None):
        """Checks if the keep-alive timer has expired and closes the connection if it has."""
        try:
            client_address = client_socket.getpeername()
        except OSError:
            print("Socket already closed.")
            return

        if self.is_keep_alive_timeout(client_socket):
            print("Keep alive time exceeded. Disconnecting the client.")
            print()

            self.ui.log_status(f"Client {client_address} disconnected due to Keep Alive timeout.")

            if self.last_will_topic and will_message:
                self.publish_will_message(self.last_will_topic, will_message, delay=5) # Publish the Last Will message after 5 seconds
            print()

            try:
                client_address = client_socket.getpeername()
            except OSError:
                print("Socket already closed.")
                return

            self.session_logger.log_session(client_address, "Disconnected due to Keep Alive")

            self.close_client_connection(client_socket)


    def is_keep_alive_timeout(self, client_socket):
        """Check if the keep-alive timer has expired for the client."""
        try:
            client_address = client_socket.getpeername()
        except OSError:
            print("Socket already closed in is_keep_alive_timeout.")
            return True  # Consider the keep-alive timeout as exceeded if the socket is closed

        last_time = self.last_keep_alive_time.get(client_address, 0)
        current_time = time.time()
        return (current_time - last_time) > self.keep_alive_value

    def close_client_connection(self, client_socket):
        """Closes the client connection due to keep-alive timeout."""
        try:
            client_address = client_socket.getpeername()  # Retrieve the client address before closing the socket
        except OSError:
            print("Socket already closed in close_client_connection.")
            return

        client_socket.close()

        if client_address in self.keep_alive_timers:
            self.keep_alive_timers[client_address].cancel()
            del self.keep_alive_timers[client_address]

        if client_address in self.last_keep_alive_time:
            del self.last_keep_alive_time[client_address]

        # print("Client connection closed.")

    def publish_will_message(self, topic, message, delay=5):
        """Publishes the Last Will message to the specified topic."""
        def send_will_message():
            print(f"Publishing LWT message to topic {topic}: {message}")
            self.store_last_message(topic, message)  # Store the Last Will message in the last_messages dictionary
            self.store_qos_message(topic, message,2)  # Store the Last Will message in the last_messages dictionary

        timer = threading.Timer(delay, send_will_message)
        timer.start()

    def handle_client(self, client_socket, address):
        """Handles the client connection and processes packets."""
        self.current_client_socket = client_socket # Store the current client socket

        val = client_socket.recv(1024)
        print("Received packet outside while:", val)
        username, password, keep_alive, will_message = PacketHandler.handle_connect(val)

        if self.db.verific_client(username, password):

            self.keep_alive_value = keep_alive - 5 # default MQTT e 60 de sec, daca vreau sa apuce si PINGREQ pun + 5
            client_socket.send(pk.connack(0x00))


            self.session_manager.create_session(username)  # Create a new session for the client

            self.reset_keep_alive_timer(client_socket, keep_alive, will_message)
            self.log_status(f"Client {username} connected from {address}.")  # Log the client disconnected message
            self.session_logger.log_session(username, "connected", {"address": address})

            # Subscribe the client to the Last Will topic
            self.session_manager.add_subscription(username, self.last_will_topic, 2)
            self.ui.log_status(f"Client {username} subscribed to Last Will topic '{self.last_will_topic}' with QoS 2.")
            self.session_logger.log_session(username, "subscribed", {"topic": self.last_will_topic, "qos": 2})
            self.add_topic_subscribed_clients(self.last_will_topic, username)

            # populez istoricul topic-urilor cu 3 topic-uri random
            random_topic1 = "Random/Topic/1"
            random_topic2 = "Random/Topic/2"
            random_topic3 = "Random/Topic/3"
            self.add_topic(random_topic1)
            self.add_topic(random_topic2)
            self.add_topic(random_topic3)

            #print(f"Keep alive value set to: {self.keep_alive_value}")
            #print("Client connected successfully.")
            #self.client_subscriptions[address] = set()

            while True:
                try:

                    data = client_socket.recv(1024)
                    print("Received packet inside while:", data)

                    if not data:
                        print("No data received, breaking the loop.")
                        break

                    # Process the received packet
                    packet_type = data[0] >> 4  # extract the packet type from the fixed header
                    print(f"Packet type: {packet_type}")  # see the packet type

                    if packet_type == 12:  # PINGREQ (0xC)
                        print("PINGREQ received.")
                        if not self.is_keep_alive_timeout(client_socket):
                            try:
                                client_socket.send(pk.pingresp())
                                print("PINGRESP sent.")
                                self.session_manager.update_last_active(username)  # actualizeaza activitatea clientului
                                self.reset_keep_alive_timer(client_socket, keep_alive, will_message)
                            except OSError as e:
                                print(f"Socket error while sending PINGRESP: {e}")
                                break
                        else:
                            print("Keep alive time exceeded. Ignoring PINGREQ.")
                            print()
                            break

                    elif packet_type == 8:  # SUBSCRIBE (0x8)
                        #print("Handling SUBSCRIBE packet.")

                        subscriptions = PacketHandler.process_subscribe_packets(data)
                        for packet_id, topic, qos in subscriptions:
                            if not self.session_manager.is_subscribed(username, topic):
                                self.session_manager.add_subscription(username, topic, qos)
                                self.session_logger.log_session(username, "subscribed", {"topic": topic, "qos": qos})
                                self.ui.log_status(f"Client {username} subscribed to topic '{topic}' with QoS {qos}.")
                                self.add_topic(topic)  # Add the topic to the list and update the UI
                                self.add_topic_subscribed_clients(topic, username)  # Add the topic and client to the list and update the UI
                                #print(f"Client {username} subscribed to topic: {topic} with QoS {qos}.")
                                client_socket.send(pk.suback(packet_id, [qos]))
                            else:
                                pass
                                #print(f"Client {username} is already subscribed to topic: {topic}. Ignoring subscription request.")
                        self.session_manager.update_last_active(username)  # actualizeaza activitatea clientului
                        self.reset_keep_alive_timer(client_socket, keep_alive, will_message)
                        print()
                        #print(self.session_manager.get_session())  # Print the session information for the client

                    elif packet_type == 3:  # PUBLISH (0x3)
                        topic, qos, payload_text, packet_id = PacketHandler.parse_publish_packet(data)

                        if not self.session_manager.is_subscribed(username, topic):
                            print(f"Client {username} is not subscribed to topic {topic}. Automatically subscribing...")
                            subscriptions = [(topic, qos)]  # list cu topicul și QoS-ul
                            self.session_manager.add_subscription(username, topic, qos)
                            subscribe_packet = pk.create_subscribe_packet(packet_id, subscriptions)
                            print(f"Generated SUBSCRIBE packet: {subscribe_packet}")
                            client_socket.send(subscribe_packet)
                            #print(f"Sending SUBSCRIBE packet.")
                            client_socket.send(pk.suback(packet_id, [qos]))

                            self.reset_keep_alive_timer(client_socket, keep_alive, will_message)
                        else:
                            # Procesează pachetul PUBLISH
                            PacketHandler.handle_publish_packet(client_socket, data)
                            self.session_manager.update_last_active(username)  # Actualizează activitatea clientului
                            self.reset_keep_alive_timer(client_socket, keep_alive, will_message)

                            # stocheaza ultimul mesaj pentru topic
                            self.store_last_message(topic, payload_text)

                            # pt QoS 1 sau 2 stochez mesajele
                            if qos in [1, 2]:
                                self.store_qos_message(topic, payload_text, qos)


                    elif packet_type == 14: # DISCONNECT (0xE)
                        if data[1] == 0x00:
                            print (f"Client {address} disconnected normally.")
                            print()
                            if address in self.keep_alive_timers:
                                self.keep_alive_timers[address].cancel() # stop the keep-alive timer
                                del self.keep_alive_timers[address] # remove the timer from the dictionary

                            self.session_logger.log_session(username, "disconnected") # log the disconnection
                            self.log_status(f"Client {username} disconnected.")  # Log the client disconnected message
                            self.session_manager.remove_session(username)  # sterge sesiunea clientului
                            client_socket.close() # close the client socket
                            break

                    else:
                        # Handle other packet types
                        pass

                except Exception as e:
                    print(f"Error processing packet: {e}")
                    #traceback.print_exc() # print the stack trace of the exception
                    self.session_logger.log_session(username, "error", {"error": str(e)})
                    self.log_status(f"Error processing packet: {e}")
                    break

        else:
            client_socket.send(pk.connack(0x86))  # Send CONNACK with Bad User Name or Password
            print("Client authentication failed, refusing connection.")
            print()
            client_socket.close()

        self.blocked_clients[address[0]] = time.time() + 3  # Block the client IP address for 3 seconds


    def start_server(self):
        """Starts the server and waits for client connections."""

        self.status = True
        while self.status:
            try:
                # print(self.server_socket)
                if self.server_socket is None:
                    self.status=False
                    break

                client_socket, address = self.server_socket.accept()
                current_time = time.time()

                if address[0] in self.blocked_clients and self.blocked_clients[address[0]] > current_time:
                    print(f"Connection attempt from blocked client {address[0]}")
                    client_socket.close()
                    continue

                elif address[0] in self.blocked_clients and self.blocked_clients[address[0]] <= current_time:
                    del self.blocked_clients[address[0]]  # Unblock the client
                print()
                print(f"Accepted connection from {address}")

                self.clients.append(client_socket)
                threading.Thread(target=self.handle_client, args=(client_socket, address)).start()

            except KeyboardInterrupt:
                self.close_server()
                self.status = False
                break

            except OSError as e:
                if self.status:  # Only print the error if the server is supposed to be running
                    print(f"Error in start_server: {e}")
                self.status = False
                break

    def close_server(self):

        """inchide socketul serverului si curata toate sesiunile active si timerele."""

        self.status = False  # asigura faptul ca nu mai dau accept la alta conexiune daca am inchis socketul
        with self.lock:

            # opreste timerele pentru fiecare client
            for client_address in list(self.keep_alive_timers.keys()):
                if client_address in self.last_keep_alive_time:
                    del self.last_keep_alive_time[client_address]

                # opreste timerul de keep_alive pentru fiecare client
                if self.keep_alive_timers[client_address].is_alive():
                    self.keep_alive_timers[client_address].cancel()  # opreste timerul
                del self.keep_alive_timers[client_address]  # sterge timerul din dictionar

            self.keep_alive_timers.clear()  # goleste dictionarul de timere

            # inchide socket-urile clientilor, sterge sesiunile si blocheaza clientii
            for client_socket in list(self.clients):
                try:
                    # verificam daca socket-ul este valid inainte de a-l inchide
                    if client_socket.fileno() != -1:
                        client_address = client_socket.getpeername()
                        print(f"Closing client socket {client_address}")
                        client_socket.close()

                except OSError as e:
                    print(f"Error while closing client socket: {e}")
                finally:
                    self.clients.remove(client_socket)

            # sterge toate sesiunile active
            for username in list(self.session_manager.get_session()):
                self.session_manager.remove_session(username)
                print(f"Session for {username} closed.")

        # inchide socket-ul serverului
            if self.server_socket is not None and self.server_socket.fileno() != -1:
                self.server_socket.close()
                self.server_socket = None  # seteaza-l la None pentru a preveni utilizarea ulterioara
                print("Server socket closed.")

    def log_status(self, message):
        if self.ui:
            self.ui.log_status(message)

    def add_topic(self, topic):
        if topic not in self.topic_history:
            self.topic_history.append(topic)
            if self.ui:
                self.ui.update_topic_history(topic)

    def add_topic_subscribed_clients(self, topic, client):
        if topic not in self.topics:
            self.topics[topic] = []
        if client not in self.topics[topic]:
            self.topics[topic].append(client)
            if self.ui:
                self.ui.update_subscribed_clients(topic, client)

    def store_last_message(self, topic, message):
        if topic not in self.last_messages:
            self.last_messages[topic] = []
        self.last_messages[topic].append(message)
        if len(self.last_messages[topic]) > 10:
            self.last_messages[topic].pop(0)
        if self.ui:
            self.ui.update_last_messages(topic, self.last_messages[topic])

    def store_qos_message(self, topic, message, qos):
        self.qos_messages.append((topic, message, qos))
        if self.ui:
            self.ui.update_qos_messages(self.qos_messages)