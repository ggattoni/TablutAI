# TablutCompetition
Software for the Tablut Students Competition

## Installazione su Ubuntu/Debian 

Queste sono le istruzioni per installare le librerie necessarie su ambiente
ubuntu/debian:

Da terminale, eseguire i seguenti comandi per installare JDK 8.

```
sudo apt update
sudo apt install openjdk-8-jdk -y
```

## Utilizzare il giocatore

Sono disponibili i jar eseguibili dei giocatori bianco e nero, rispettivamente chiamati `TablutWhiteClient.jar` e `TablutBlackClient.jar`. Per avviare i giocatori è sufficiente usare la seguente istruzione:

```
java -jar TablutWhiteClient.jar
java -jar TablutBlackClient.jar
```

In questo modo verranno eseguiti i giocatori con i parametri di default, ovvero: un timeout di 60 secondi, 2 processori a disposizione e come indirizzo del server `localhost`.

Per modificare questi parametri è sufficiente utilizzare le diverse opzioni disponibili nell'interfaccia a riga di comando. A titolo di esempio verrà mostrato l'utilizzo delle opzioni solo per il giocatore bianco, ma le opzioni del giocatore nero sono identiche.

```
java -jar TablutWhiteClient.jar [-t <timeout>] [-a <address>] [-p <CPUs>]
```

- Con l'opzione -t è possibile cambiare il numero di secondi del timeout (default: 60).
- Con l'opzione -a è possibile cambiare l'indirizzo del server (default: localhost).
- Infine, con l'opzione -p è possibile cambiare il numero di processori che ha a disposizione il giocatore (default: 2).
