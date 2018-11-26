README - Tema 2 APD - Cherechesu Andrei, 322CB

La implementarea temei s-a folosit o structura de date ajutatoare,
auto-sincronizata, LinkedBlockingQueue.

Se citesc cate 2 mesaje de pe WizardChannel de catre mineri, cu ajutorul
functiei getMessageSetWizardChannel, ce face synchronized(this),
pentru a nu se intercala citirile.

S-a folosit ReentrantLock pentru a asigura faptul ca Wizards pun cate
2 mesaje consecutive pe wizardChannel. Lock-ul isi se elibereaza
dupa punerea celui de-al doilea mesaj. Al doilea mesaj se pune
daca lock-ul e tinut de thread-ul curent. Daca se poate face lock,
atunci inseamna ca trebuie trimis primul mesaj, si se face lock.

Se hash-uiesc string-urile de catre mineri de hashCount ori si se tirmit
inapoi la wizards pe Miner Channel, daca camera nu a fost deja rezolvata,
sau daca mesajul primit nu avea ca data string "EXIT".

Orice alta precizare suplimentara este in comentariile codului,
desi codul este usor de urmarit chiar si fara comentarii.

