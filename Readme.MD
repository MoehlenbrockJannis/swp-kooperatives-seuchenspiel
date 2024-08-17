# SWP Basissystem	
===============

Der folgende Code sollte als Basis für das Software Projekt verwendet werden.


## Setup - Maven
-----
Das Projekt kann mit Maven gebaut werden.
Dafür müssen Java und Maven im Path des Systems hinterlegt sein.

Entweder kann dazu die Datei [start.bat](start.bat) ausgeführt werden oder der folgende Prozess durchgeführt werden.

### Common

Auf der Hauptebene zunächst folgenden Befehl in einem Terminal aufrufen, um die Dependencies aus der [pom-Datei](pom.xml) zu aktualisieren:

<pre>
    <code>
        mvn clean install
    </code>
</pre>


### Server

Um den Server zu bauen:
<pre>
    <code>
        cd server
        mvn clean package
    </code>
</pre>

Der Server ist dann unter target zu finden und kann z.B. wie folgt aufgerufen werden:

<pre>
    <code>
        cd target
        java -jar server-1.0.0-jar-with-dependencies.jar
    </code>
</pre>


### Client

Um den Client zu bauen:
<pre>
    <code>
        cd client
        mvn clean package
    </code>
</pre>

Der Client ist dann unter target zu finden und kann z.B. wie folgt aufgerufen werden:

<pre>
    <code>
        cd target
        java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar
    </code>
</pre>


### Intellij

Zum Starten in Intellij müssen Run Configurations erstellt werden.
Um in das Menü zum Hinzufügen zu gelangen, müssen folgende Aktionen durchgeführt werden:
1. Links neben dem Startknopf auf "Run" drücken.
2. "Edit Configurations..." auswählen.
3. +-Knopf ("Add New Configuration") drücken

In diesem Menü wird ein Shell Script für den Server erstellt:
- "Shell Script" auswählen:
    - Name: "Start Backend"
    - Script path: "C:\Path\to\start-server.bat"

Danach wird ein Shell Script für den Client erstellt:
- "Shell Script" auswählen:
    - Name: "Start Frontend"
    - Script path: "C:\Path\to\start-client.bat"

Um sowohl Server als auch Client mit einem Knopfdruck zu starten, muss ein Compound erstellt werden:
- "Compound" auswählen:
    - Name: "Start Backend and Frontend"
    - +-Knopf im Compound ("Add") drücken und "Start Backend" sowie "Start Frontend" in dieser Reihenfolge hinzufügen

Weitere Hinweise finden sich im Wiki https://confluence.swl.informatik.uni-oldenburg.de/display/SWP/Vorlesungsvideos