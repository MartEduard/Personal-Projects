import threading
import time

class SessionManager:
    def __init__(self, expiration_time=180, cleanup_interval=90):
        """
        expiration_time: timpul (in secunde) dupa care o sesiune expira.
        cleanup_interval: intervalul (in secunde) pentru curatarea sesiunilor expirate.
        """
        self.sessions = {}  # dictionar pentru a stoca sesiunile active
        self.expiration_time = expiration_time
        self.lock = threading.Lock()  # protejeaza accesul concurent la sesiuni
        self.cleanup_interval = cleanup_interval
        self.running = True  # flag pentru oprirea thread-ului
        # daemon=True permite thread-ului sa se opreasca automat cand programul principal se opreste
        self.cleanup_thread = threading.Thread(target=self._cleanup_sessions, daemon=True)
        self.cleanup_thread.start()

    def create_session(self, client_id, client_address=None, clean_start=False):
        """creeaza o sesiune noua sau reutilizeaza una existenta."""
        with self.lock:
            if client_id in self.sessions and not clean_start:
                self.sessions[client_id]["last_active"] = time.time()
                self.sessions[client_id]["client_address"] = client_address
                print(f"Reusing existing session for client {client_id}.")
            else:
                self.sessions[client_id] = {
                    "subscriptions": {},
                    "last_active": time.time(),
                }
                print(f"Created a new session for client {client_id}.")
                print()

    # def add_subscription(self, client_id, topic, qos):
    #     """adauga o abonare la un topic pentru un client."""
    #     with self.lock:
    #         if client_id in self.sessions:
    #             self.sessions[client_id]["subscriptions"][topic] = qos

    def add_subscription(self, client_id, topic, qos):
        """adauga o abonare la un topic pentru un client."""
        with self.lock:
            if client_id in self.sessions:
                subscriptions = self.sessions[client_id]["subscriptions"]
                if topic not in subscriptions:
                    subscriptions[topic] = qos
                    print(f"Client {client_id} subscribed to topic '{topic}' with QoS {qos}.")
                else:
                    print(f"Client {client_id} is already subscribed to topic '{topic}'.")

    def is_subscribed(self, client_id, topic):
        """verifica daca un client este abonat la un anumit topic."""
        with self.lock:
            return topic in self.sessions.get(client_id, {}).get("subscriptions", {})


    def update_last_active(self, client_id):
        """actualizeaza timpul ultimului mesaj primit de la un client."""
        with self.lock:
            if client_id in self.sessions:
                self.sessions[client_id]["last_active"] = time.time()

    def remove_session(self, client_id):
        """sterge sesiunea unui client."""
        with self.lock:
            if client_id in self.sessions:
                del self.sessions[client_id]

    def _cleanup_sessions(self):
        """thread care verifica periodic sesiunile expirate."""
        while self.running:
            with self.lock:
                current_time = time.time()
                expired_clients = [
                    client_id for client_id, session in self.sessions.items()
                    if current_time - session["last_active"] > self.expiration_time
                ]
                for client_id in expired_clients:
                    print(f"Session expired for client {client_id}. Removing information about current session.")
                    print()
                    del self.sessions[client_id]
            time.sleep(self.cleanup_interval)

    def stop_cleanup_thread(self):
        """opreste thread-ul de curatare."""
        self.running = False
        self.cleanup_thread.join()

    def get_session(self, username=None):
        """returneaza informatiile despre sesiunea unui utilizator sau toate sesiunile."""
        with self.lock:
            if username:
                return self.sessions.get(username, f"Sesiunea pentru {username} nu exista.")
            return self.sessions  # Returneaza toate sesiunile active
