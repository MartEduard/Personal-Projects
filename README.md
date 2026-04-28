# **Broker MQTT v5.0**


  **MQTT (Message Queuing Telemetry Transport)** este un protocol de transport de mesaje bazat pe modelul Client-Server de tip publish/subscribe. În rețelistică, un protocol este un set de reguli pentru formatarea și procesarea datelor. Protocoalele de rețea sunt asemenea unei limbi comune pentru calculatoare, astfel că, indiferent de diferențele hardware sau software, utilizarea protocoalelor le permite să comunice între ele.

  Protocolul MQTT a fost conceput la sfârșitul anilor 1990 de Andy Stanford-Clark de la IBM și Arlen Nipper de la Cirrus Link, pentru monitorizarea conductelor de petrol și gaze prin rețele de satelit. Este ușor, deschis, simplu și conceput pentru a fi ușor de implementat. Designul său a permis conectarea a mii de dispozitive la un singur server MQTT, făcându-l potrivit pentru utilizarea în medii constrânse, cum ar fi comunicația în contextul Machine to Machine (M2M) și Internet of Things (IoT), unde este necesar un cod de dimensiuni reduse și/sau lățimea de bandă a rețelei este limitată. 

  Protocolul MQTT funcționează în contextul stivei de protocoale TCP/IP sau pe alte protocoale de rețea care asigură conexiuni bidirecționale, fără pierderi și care păstrează ordinea mesajelor. **TCP/IP** reprezintă **Transmission Control Protocol/Internet Protocol** și este un set de protocoale de comunicație utilizat pentru interconectarea dispozitivelor de rețea pe internet. Datele transportate de protocolul **MQTT** prin rețea sunt destinate aplicației. Când un mesaj de aplicație este transmis prin **MQTT**, acesta conține datele încărcăturii (payload), un nivel de calitate a serviciului (QoS), un set de proprietăți și un nume de subiect (Topic Name).

![presentation](https://github.com/user-attachments/assets/5c6f7c5a-5eba-41c2-935a-91a73c0d099c)


<br> **Arhitectura Publish/Subscribe:**

  **Publish/Subscribe** oferă un cadru pentru schimbul de mesaje între **publisheri** (componente care creează și trimit mesaje) și **abonați** (componente care primesc și consumă mesaje). Este important de menționat că publicatorii nu trimit mesaje către abonați specifici într-o manieră punct-la-punct. În schimb, este utilizat un intermediar — un **broker** de mesaje **Publish/Subscribe**, care grupează mesajele în entități numite canale (sau topicuri). 

  Termenul „subiect” (topic) se referă la cuvintele cheie pe care brokerul **MQTT** le folosește pentru a filtra mesajele destinate clienților **MQTT**. Subiectele sunt organizate ierarhic, similar unui director de fișiere sau foldere. De exemplu, dacă vom considera un sistem ce operează într-o casă cu mai multe etaje, ce are diferite dispozitive inteligente, un topic poate arăta în felul următor: *ourhome/groundfloor/livingroom/light*

![pub_sub](https://github.com/user-attachments/assets/aa51a4a0-fb0a-4a55-bfeb-8f5977436a79)


<br> **Broker:**

  **Brokerul** MQTT este sistemul backend (serverul) care coordonează mesajele între diferiții clienți. Responsabilitățile brokerului includ primirea și filtrarea mesajelor, identificarea clienților abonați la fiecare mesaj și trimiterea acestora. De asemenea, este responsabil pentru alte sarcini, cum ar fi:

  **Gestionarea sesiunilor:** Brokerii MQTT pot menține datele de sesiune pentru toți clienții conectați, inclusiv abonamentele și mesajele pierdute, pentru clienții cu sesiuni persistente.

  **Autentificare și autorizare:** Brokerul este responsabil pentru autentificarea și autorizarea clienților pe baza credențialelor furnizate de aceștia. Brokerul este extensibil, facilitând autentificarea și autorizarea personalizate, precum și integrarea în sistemele backend. În plus față de autentificare și autorizare, brokerii pot oferi alte caracteristici de securitate, cum ar fi criptarea mesajelor în tranzit și listele de control al accesului.

  **Scalabilitate și monitorizare:** Un broker MQTT trebuie să fie scalabil pentru a gestiona volume mari de mesaje și clienți, să se integreze în sistemele backend, să fie ușor de monitorizat și să fie rezistent la eșecuri. Pentru a îndeplini aceste cerințe, brokerul MQTT trebuie să utilizeze procesarea rețelelor bazată pe evenimente de ultimă generație, un sistem deschis de extensie și furnizori standard de monitorizare. Brokerii pot oferi, de asemenea, caracteristici avansate pentru gestionarea și monitorizarea sistemului MQTT, cum ar fi filtrarea mesajelor, persistența mesajelor și analizele în timp real.

  **Clustering:** Brokerii MQTT pot suporta clustering, permițând mai multor instanțe ale brokerului să lucreze împreună pentru a gestiona un număr mare de clienți și mesaje.

  **Controlul fluxului de date IoT în cadrul pipeline-ului tău de date:** Brokerii MQTT proeminenți îți pot oferi capacitatea de a avea control asupra datelor tale prin instrumente de vizualizare și gestionare, caracteristici puternice de securitate și un motor de politici integrat care poate valida, impune și transforma datele în mișcare.

![broker](https://github.com/user-attachments/assets/3acd4936-466c-4ce8-89b6-927b03653b93)

<br> **Formatul pachetelor de control MQTT:**

Protocolul MQTT funcționează pe baza schimbului de **Pachete de Control** într-o manieră bine definită.


1. **Fixed Header** – este prezent în toate pachetele de control și conține informații de bază, cum ar fi tipul pachetului (biții 7-4), flag-uri specifice tipului de pachet (biții 3-0) și lungimea rămasă. 
<br><br> ![packet_format](https://github.com/user-attachments/assets/f03df94e-ecb5-49b9-8fff-a4090709560a) <br>
2. **Variable Header** – apare doar în anumite pachete de control, cum ar fi pachetele CONNECT, PUBLISH, SUBSCRIBE etc., unde sunt necesare informații suplimentare, cum ar fi identificatorii sau proprietățile.
3. **Payload** – este prezent în pachete de control care transportă date pentru aplicații sau informații de configurare. De exemplu, în pachetul PUBLISH, Payload-ul conține datele efective care sunt transmise între client și broker.

De exemplu, pentru pachetul CONNECT avem formatul: **0001 0000** (tip CONNECT și flag-ul 0), **Remaining Length** variază în funcție de Variable Header și Payload.

<b>Pe lângă aceste pachete de bază, MQTT v5.0 aduce în plus și:</b>



* **User Properties** – perechi de tip cheie-valoare pentru a adăuga informații personalizate la mesaje și pachete de control.
* **Session Expiry Interval** – Acesta este un interval de timp, specificat în secunde, care determină durata de viață a sesiunii unui client. După expirarea acestui interval, brokerul va șterge toate informațiile asociate cu sesiunea clientului, inclusiv mesajele neîndeplinite.
* **Reason Codes** –o valoare fără semn de un byte care indică rezultatul unei operațiuni. Reason Codes mai mici decât 0x80 indică finalizarea cu succes a unei operațiuni. Reason Code-ul normal pentru succes este 0.


<br> **Mecanisme MQTT v5.0:**

1. **Keep Alive**

  "Keep Alive" este un întreg pe doi octeți, reprezentând un interval de timp în secunde. Este intervalul maxim permis între două pachete de control MQTT trimise de Client. Clientul trebuie să respecte acest interval, iar dacă Keep Alive este diferit de zero și nu sunt trimise alte pachete, Clientul trebuie să trimită un PINGREQ. **PINGREQ** \- Pachet de tip "Ping Request" trimis de Client către Server pentru a verifica disponibilitatea acestuia.

  Dacă Serverul oferă un "Server Keep Alive" în pachetul CONNACK, Clientul trebuie să folosească acea valoare în locul valorii sale. **CONNACK** \- Pachet de confirmare a conexiunii trimis de Server către Client.

  Clientul poate trimite un PINGREQ oricând pentru a verifica disponibilitatea rețelei și Serverului. Dacă Serverul nu primește un pachet MQTT într-un interval de 1,5 ori valoarea Keep Alive, acesta va închide conexiunea. Dacă Clientul nu primește un PINGRESP într-un timp rezonabil după PINGREQ, va încheia conexiunea. **PINGRESP** \- Răspunsul la PINGREQ, trimis de Server pentru a confirma disponibilitatea. O valoare Keep Alive de 0 dezactivează acest mecanism, iar Clientul nu trebuie să trimită pachete după un program anume.


2. **Last will**

  "Last Will and Testament" (LWT) este o funcționalitate puternică în MQTT care permite clienților să specifice un mesaj ce va fi publicat automat de broker în numele lor, în cazul unei deconectări neașteptate. Funcția este deosebit de valoroasă atunci când clienții trebuie să notifice alte entități despre indisponibilitatea lor sau să transmită informații importante în urma unei deconectări neprevăzute. 

  Este esențial să se înțeleagă tipul deconectării (graceful \- cu un mesaj de deconectare sau ungraceful \- fără un mesaj de deconectare) pentru a lua măsuri adecvate.

  LWT le permite clienților să specifice un mesaj de „last will” atunci când se conectează la un broker. Mesajul include structura unui mesaj MQTT obișnuit: un subiect (topic), flag-ul de reținere a mesajului (retained message flag), Quality of Service (QoS) și un payload. Brokerul stochează acest mesaj până când detectează o deconectare necontrolată de la client. La detectarea deconectării, brokerul transmite mesajul LWT tuturor clienților abonați la subiectul respectiv. Brokerul elimină mesajul LWT stocat dacă clientul se deconectează în mod controlat, utilizând mesajul DISCONNECT.


3. **Quality of Service(QoS):**

  **Quality of Service (QoS)** este un acord între expeditorul și receptorul mesajului care definește nivelul de garanție a livrării pentru un mesaj specific.

  QoS este crucial în MQTT datorită rolului său în oferirea clientului capacitatea de a selecta un nivel de serviciu care se aliniază atât cu fiabilitatea rețelei, cât și cu cerințele aplicației. Capacitatea MQTT de a gestiona retransmisia mesajelor și de a asigura livrarea, chiar și în condiții de rețea nesigure, face ca QoS să fie esențial pentru facilitarea comunicării fără întreruperi în astfel de medii dificile.

  **QoS 0** **(At most once delivery)**. Nu se trimite niciun răspuns de către receptor și nu se efectuează nicio încercare de retrimitere din partea expeditorului. Mesajul ajunge la receptor fie o dată, fie deloc. QoS 0, cunoscut adesea sub denumirea de „fire and forget”, funcționează asemănător cu protocolul TCP de bază, unde mesajul este trimis fără urmărire sau confirmare ulterioară.

![QoS0](https://github.com/user-attachments/assets/a94137e2-0d05-4f08-aeff-3dabe6a2a1a0)

  **QoS 1 (At least once delivery).** Accentul este pe asigurarea livrării mesajului de cel puțin o dată către receptor. Atunci când un mesaj este publicat cu QoS 1, expeditorul păstrează o copie a mesajului până când primește un pachet PUBACK de la receptor, care confirmă primirea cu succes. Dacă expeditorul nu primește pachetul PUBACK într-un interval de timp rezonabil, acesta retransmite mesajul pentru a asigura livrarea. 

![QoS1](https://github.com/user-attachments/assets/ffe435fc-077d-4d72-a357-35bdc7d9d8d7)

  **QoS 2 (Exactly Once Delivery)** oferă cel mai înalt nivel de serviciu în MQTT, asigurându-se că fiecare mesaj este livrat exact o dată către destinatarii intenționați. Pentru a realiza acest lucru, QoS 2 implică o procedură de confirmare în patru pași între expeditor și receptor.

1. **Primirea mesajului:** Când receptorul primește un pachet PUBLISH QoS 2, procesează mesajul și răspunde cu un pachet PUBREC pentru a confirma primirea.

2. **Retransmiterea:** Dacă expeditorul nu primește PUBREC, retrimite pachetul PUBLISH cu flagul de duplicat (DUP) până când primește confirmarea.

3. **Confirmarea:** Odată ce expeditorul primește pachetul PUBREC, poate elimina pachetul PUBLISH inițial și trimite un pachet PUBREL. Receptorul, la rândul său, elimină stările stocate și răspunde cu un pachet PUBCOMP.

4. **Finalizarea:** După ce receptorul trimite pachetul PUBCOMP, identificatorul pachetului mesajului devine disponibil pentru reutilizare.

![QoS2](https://github.com/user-attachments/assets/3707611a-7ea7-47d2-985a-6def4c1be4bd)

<br> **Diagrama de secvență:** <br>

![sequence_diagram](https://github.com/user-attachments/assets/beff464d-85d3-422e-824d-519fe62f0565)

Am implementat interfața grafică pentru vizualizarea istoric topicuri, istoricul ultimelor 10 mesaje publicate, topicuri cu clienți abonați, mesaje stocate cu QoS0, 1 și 2. De asemenea, am introdus și mecanismul pentru autentificarea clienților, mecanismul Keep Alive, mecanismul Last Will (delayed), Quality of Service 0, 1 și 2, și stocarea sesiunilor cu posibilitatea de expirare.
Lucruri notabile de menționat la proiectul nostru:

1. **Stocarea sesiuni:**
Stocarea sesiunilor în proiectul curent este gestionată de clasa SessionManager din fișierul sessions.py. Aceasta clasă se ocupă de crearea, actualizarea și ștergerea sesiunilor pentru clienți, precum și de curățarea periodică a sesiunilor expirate.
Fișierul session_logger.py conține clasa SessionLogger, care este responsabilă pentru logarea activităților unei sesiuni într-un fișier JSON. Clasa include metode pentru inițializarea loggerului și înregistrarea acțiunilor sesiunii cu detalii precum username, action, timestamp si details. 

![Session](https://github.com/user-attachments/assets/9be30082-ff51-454e-8c4d-48403176f04e)

2. **Interfața grafică cu utilizatorul:**
Clasa GUI din user_interface.py include metode pentru inițializarea interfeței, crearea tab-urilor pentru diferite funcționalități (cum ar fi statusul serverului, istoricul topicurilor, ultimele 10 mesaje publicate, clienții abonați la topicuri și mesajele QoS), și butoane de control pentru pornirea și oprirea serverului. De asemenea, clasa gestionează logarea mesajelor de status și actualizarea listelor de topicuri și mesaje afișate în interfață.

![GUI_1](https://github.com/user-attachments/assets/bc741b55-2af6-4841-b6f2-f603599c4d1d)

![GUI_2](https://github.com/user-attachments/assets/c6bd8349-12d7-4fb8-92ec-bb8f3199ef3f)

![GUI_3](https://github.com/user-attachments/assets/e4446bad-c90e-421e-b846-53c3618af6c4)

![GUI_4](https://github.com/user-attachments/assets/bb7e15c6-ba6b-4183-8b44-3ebc1e91bf46)

![GUI_5](https://github.com/user-attachments/assets/badb3bb2-2a65-4306-8ffb-44be08b51891)

![WireShark](https://github.com/user-attachments/assets/4bf8d6d8-b9ba-4aea-b8e4-62ab1dfea84f)

3. **Autentificarea clienților:**
Autentificarea este realizată verificând în fișierul auth_package.json datele clienților prin intermediul clasei DB. Aceasta citește din fișierul JSON username-ul și parola, și le salvează într-o tuple, ca ulterior să fie verificat de metoda verific_client credențialele clientului care se conectează la server cu ajutorul unui simulator de client MQTT.

![Credentiale](https://github.com/user-attachments/assets/070b65d5-4834-4229-b162-f563b6ea6e5f)

![Explorer_1](https://github.com/user-attachments/assets/003a10a8-b835-43e9-a1a5-f531a2b0328e)

![Explorer_2](https://github.com/user-attachments/assets/f06afe22-a537-451e-8bcd-bec0ff5a07fd)

![Explorer_3](https://github.com/user-attachments/assets/4ad206f6-079c-4a04-9fd3-6677fb2657d1)


**Bibliografie:**

[https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html](https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html)  
[https://www.techtarget.com/searchnetworking/definition/TCP-IP](https://www.techtarget.com/searchnetworking/definition/TCP-IP)
[https://ably.com/topic/pub-sub\#pub-sub-architecture](https://ably.com/topic/pub-sub#pub-sub-architecture)  
[https://www.hivemq.com/mqtt/mqtt-5/](https://www.hivemq.com/mqtt/mqtt-5/)  
[https://aws.amazon.com/what-is/mqtt/](https://aws.amazon.com/what-is/mqtt/#:~:text=The%20MQTT%20broker%20is%20the,and%20sending%20them%20the%20messages)  
[https://mobidev.biz/blog/mqtt-5-protocol-features-iot-development](https://mobidev.biz/blog/mqtt-5-protocol-features-iot-development) <br>
[https://mqtt.org](https://mqtt.org)




