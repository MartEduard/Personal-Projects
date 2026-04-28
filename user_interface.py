import threading
import tkinter as tk
from tkinter import ttk, messagebox # ttk este un modul care contine widget-uri imbunatatite fata de cele din tkinter (ex: Notebook)
import random
from server import ServerMQTT
import packets as pk

class GUI(tk.Tk):
    def __init__(self, server_instance):
        super().__init__()

        self.server_instance = server_instance  # referinta la server
        self.server_thread = None  # referinta la thread-ul serverului

        self.title("MQTT Server Dashboard")
        self.geometry("800x600")

        # tab control
        self.tab_control = ttk.Notebook(self)  # Notebook este un container pentru tab-uri in tkinter

        # tab for status messages
        self.status_tab = ttk.Frame(self.tab_control)
        self.tab_control.add(self.status_tab, text="Server Status")

        # tab for topic history
        self.topic_history_tab = ttk.Frame(self.tab_control)
        self.tab_control.add(self.topic_history_tab, text="Topic History")

        # tab for last 10 published messages
        self.last_messages_tab = ttk.Frame(self.tab_control)
        self.tab_control.add(self.last_messages_tab, text="Last 10 Published Messages")

        # tab for subscribed clients
        self.subscribed_clients_tab = ttk.Frame(self.tab_control)
        self.tab_control.add(self.subscribed_clients_tab, text="Topics with Subscribed Clients")

        # Tab for QoS Messages
        self.qos_messages_tab = ttk.Frame(self.tab_control)
        self.tab_control.add(self.qos_messages_tab, text="QoS1/QoS2 Messages")

        # Display the tabs
        self.tab_control.pack(expand=1, fill="both")

        self.create_status_tab()
        self.create_topic_history_tab()
        self.create_last_messages_tab()
        self.create_subscribed_clients_tab()
        self.create_qos_messages_tab()
        self.create_control_buttons()  # Adaugă butoane de control

        self.protocol("WM_DELETE_WINDOW", self.on_closing)

    def create_status_tab(self):
        """Create UI elements for Server Status tab."""
        label = tk.Label(self.status_tab, text="Server Status Messages", font=("Helvetica", 16))
        label.pack(pady=10)

        self.status_listbox = tk.Listbox(self.status_tab, height=25, width=100)
        self.status_listbox.pack(pady=20)

    def log_status(self, message):
        """Adauga un mesaj la lista de status."""
        self.status_listbox.insert(tk.END, message)
        self.status_listbox.insert(tk.END, "")
        self.status_listbox.yview(tk.END)  # asigura ca se va scrolla automat la ultimul mesaj

    def create_control_buttons(self):
        """Creates buttons for a better control."""
        button_frame = tk.Frame(self)
        button_frame.pack(pady=20, fill="x")

        # buton de Start
        self.start_button = tk.Button(button_frame, text="Start Server", command=self.start_server)
        self.start_button.pack(side="left", padx=20)

        # buton de Oprire
        self.close_button = tk.Button(button_frame, text="Close Server", command=self.close_server)
        self.close_button.pack(side="right", padx=20)

        self.show_messages_button = tk.Button(button_frame, text="Show Last 10 Messages",command=self.show_last_messages)
        self.show_messages_button.pack(side="bottom", padx=20)

    def close_server(self):
        """Closes the server and the gui."""
        if self.server_instance:
            #print("Closing the server inside GUI.")
            self.server_instance.close_server()
            if self.server_thread:
                self.server_thread.join()  # Ensure the server thread has finished
            self.server_instance = None
            self.log_status("Server closed.")  # Log the server closed message

    def start_server(self):
        if not self.server_instance:
            self.server_instance = ServerMQTT(self)  # Recreate the server instance if it was closed
        if not self.server_thread or not self.server_thread.is_alive():
            self.server_thread = threading.Thread(target=self.server_instance.start_server)
            self.server_thread.daemon = True  # Ensure the server thread closes when the main program exits
            self.server_thread.start()
            self.log_status("Server started.")  # Log the server started message
            #print("Server started.")

    def on_closing(self):
        self.close_server()
        self.destroy()

    def create_topic_history_tab(self):
        """Create UI elements for Topic History tab."""
        label = tk.Label(self.topic_history_tab, text="Istoric Topicuri", font=("Helvetica", 16))
        label.pack(pady=10)

        self.topic_listbox = tk.Listbox(self.topic_history_tab, height=25, width=100)
        self.topic_listbox.pack(pady=20)

    def update_topic_history(self, topic):
        """Update the topic history list."""
        self.topic_listbox.insert(tk.END, topic)
        self.topic_listbox.insert(tk.END, "")
        self.topic_listbox.yview(tk.END)  # Ensure the listbox scrolls to the last topic

    def create_last_messages_tab(self):
        """Create UI elements for Last 10 Published Messages tab."""
        label = tk.Label(self.last_messages_tab, text="Last 10 Published Messages are: ", font=("Helvetica", 16))
        label.pack(pady=10)

        self.last_messages_listbox = tk.Listbox(self.last_messages_tab, height=25, width=100)
        self.last_messages_listbox.pack(pady=20)

    def update_last_messages(self, topic, messages):
        self.last_messages_listbox.insert(tk.END, f"Topic: {topic}")
        for message in messages:
            self.last_messages_listbox.insert(tk.END, f"-> {message}")
        self.last_messages_listbox.yview(tk.END)

    def show_last_messages(self):
        if self.server_instance:
            last_messages = self.server_instance.last_messages
            self.last_messages_listbox.delete(0, tk.END)  # Clear the listbox
            for topic, messages in last_messages.items():
                self.last_messages_listbox.insert(tk.END, f"Topic: {topic}")
                for message in messages:
                    self.last_messages_listbox.insert(tk.END, f"-> {message}")
                self.last_messages_listbox.insert(tk.END, "")  # Add a blank line for separation
            self.last_messages_listbox.yview(tk.END)

    def create_subscribed_clients_tab(self):
        """Create UI elements for Subscribed Clients tab."""
        label = tk.Label(self.subscribed_clients_tab, text="The Topics with Subscribed Clients: ", font=("Helvetica", 16))
        label.pack(pady=10)

        self.subscribed_clients_listbox = tk.Listbox(self.subscribed_clients_tab, height=25, width=100)
        self.subscribed_clients_listbox.pack(pady=20)

    def update_subscribed_clients(self, topic, client):
        self.subscribed_clients_listbox.insert(tk.END, f"Topic: {topic}, Client: {client}")
        self.subscribed_clients_listbox.insert(tk.END, "")
        self.subscribed_clients_listbox.yview(tk.END)

    def create_qos_messages_tab(self):
        """Create UI elements for QoS Messages tab."""
        label = tk.Label(self.qos_messages_tab, text="QoS1/QoS2 messages:", font=("Helvetica", 16))
        label.pack(pady=10)

        self.qos_messages_listbox = tk.Listbox(self.qos_messages_tab, height=25, width=100)
        self.qos_messages_listbox.pack(pady=20)

    def update_qos_messages(self, qos_messages):
        self.qos_messages_listbox.delete(0, tk.END)
        for topic, message, qos in qos_messages:
            self.qos_messages_listbox.insert(tk.END, f"Topic: {topic}, QoS: {qos}")
            self.qos_messages_listbox.insert(tk.END, f"-> Message: {message}")
        self.qos_messages_listbox.yview(tk.END)