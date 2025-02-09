# SWP Basissystem

Der folgende Code sollte als Basis für das Software Projekt verwendet werden.


## Setup - Maven

Das Projekt kann mit Maven gebaut werden.
Dafür müssen Java und Maven im Path des Systems hinterlegt sein.

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

Zum Starten in Intellij können Run Configurations erstellt werden.
Um in das Menü zum Hinzufügen zu gelangen, müssen folgende Aktionen durchgeführt werden:
1. Links neben dem Startknopf auf "Run" drücken.
2. "Edit Configurations..." auswählen.
3. +-Knopf ("Add New Configuration") drücken

Ein Compound aus folgenden Komponenten kann erstellt werden:
1. JAR Application "Start Backend":
   - Path to JAR: "C:\Path\to\SWP\server\target\server-1.0.0-jar-with-dependencies.jar"
   - Before launch:
     1. Maven "Base [clean,install]":
        - Run: "clean install"
        - Working directory: "C:\Path\to\SWP"
     2. Maven "Backend [clean,package]":
        - Run: "clean package"
        - Working directory: "C:\Path\to\SWP\server"
2. JAR Application "Start Frontend":
   - Path to JAR: "C:\Path\to\SWP\client\target\client-1.0-SNAPSHOT-jar-with-dependencies.jar"
   - Before launch:
     1. Maven "Frontend [clean,package]":
         - Run: "clean package"
         - Working directory: "C:\Path\to\SWP\client"
## Setup - Datenbank

Die Datenbank ist dauerhaft über eine Azure-VM erreichbar.

- Im Projekt befindet sich eine `.env.example` Datei, dies muss kopiert werden und in `.env` umbenannt werden.
- In der müssen die dort angegebenen Variablen mit den entsprechenden Werten gefüllt werden.
## Setup - Docker

Der Server kann in einem Docker-Container gestartet werden. Das dazu notwendige Image kann über Maven gebaut werden.

### Voraussetzungen

1. Docker muss lokal installiert sein. Eine Anleitung dazu findet sich [hier](https://docs.docker.com/get-docker/).

### Erstellung

#### Lokal

Um das Docker-Image zu erstellen, muss folgender Befehl ausgeführt werden:
```shell
mvn clean package 
```
Soll ein Docker-Image für den Raspberry Pi erstellt werden, muss folgender Befehl ausgeführt werden:
```shell
mvn clean package -D image.tag.value=git.swp-ibs.de:5050/swp/2024/gb/pandemie-swp/arm/server -D platform.value=linux/arm64
```

#### Mit push in die GitLab-Registry

Soll das Docker-Image erstellt werden und in die GitLab-Registry hochgeladen werden, muss folgender Befehl ausgeführt werden:
```shell
docker login git.swp-ibs.de:5050
```
Daraufhin wird nach Benutzername und Passwort von GitLab gefragt. Anschließend kann das Image erstellt und gepusht werden:
```shell
mvn clean install 
```
Auch hier kann ein Docker-Image für den Raspberry Pi erstellt und gepusht werden:
```shell
mvn clean install -D image.tag.value=git.swp-ibs.de:5050/swp/2024/gb/pandemie-swp/arm/server -D platform.value=linux/arm64
```
### Starten

Um den Server in einem Docker-Container zu starten, muss folgender Befehl ausgeführt werden:
```shell
docker run -p 8899:8899 -d <tag>
```
- Tag PC / Server = git.swp-ibs.de:5050/swp/2024/gb/pandemie-swp/server:\<Versions-Nr>
- Tag Pi = git.swp-ibs.de:5050/swp/2024/gb/pandemie-swp/arm/server:\<Versions-Nr>


Weitere Hinweise finden sich im Wiki https://confluence.swl.informatik.uni-oldenburg.de/display/SWP/Vorlesungsvideos