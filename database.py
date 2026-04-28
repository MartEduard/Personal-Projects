import json

class DB:
    def __init__(self):
        # initializeaza lista de clients
        self.clienti = []
        # defineste calea catre fisierul json cu credentiale
        cale_de_acces = r".\auth_package.json"
        # deschide si incarca datele din fisierul json
        with open(cale_de_acces, "r") as fisier:
            data = json.load(fisier)

        # extrage credentialele clientilor
        credentiale = data.get('credentiale', [])
        
        for client in credentiale:
            # obtine numele si parola fiecarui client
            nume_client = client.get('nume_client')
            parola_client = client.get('parola_client')
            # adauga tuplele (nume, parola) in lista clientilor
            self.clienti.append((nume_client, parola_client))

    def verific_client(self, nume_client, parola_client):
        # verifica daca (nume_client, parola_client) se afla in lista clientilor
        if (nume_client, parola_client) in self.clienti:
            return True
        else:
            return False

    def get_client_credentials(self):
        # metoda rezervata pentru obtinerea credentialelor clientilor (neimplementata)
        pass
