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

È disponibile il jar eseguibile contenente i giocatori bianco e nero chiamato `TeleTabBIT.jar`. Per avviare i giocatori è sufficiente usare la seguente istruzione:

```
# Per eseguire il giocatore bianco
java -jar TeleTabBIT.jar white

# Per eseguire il giocatore nero
java -jar TeleTabBIT.jar black
```

In questo modo verranno eseguiti i giocatori con i parametri di default, ovvero: un timeout di 60 secondi, 2 processori a disposizione e come indirizzo del server `localhost`.

Per modificare questi parametri è sufficiente utilizzare le diverse opzioni disponibili nell'interfaccia a riga di comando.

```
java -jar TeleTabBIT.jar [white | black] [-t <timeout>] [-a <address>] [-p <CPUs>]
```

- Con l'opzione -t è possibile cambiare il numero di secondi del timeout (default: 60).
- Con l'opzione -a è possibile cambiare l'indirizzo del server (default: localhost).
- Infine, con l'opzione -p è possibile cambiare il numero di processori che ha a disposizione il giocatore (default: 2).
