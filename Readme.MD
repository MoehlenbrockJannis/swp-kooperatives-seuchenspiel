# Pandemie

Pandemie ist eine digitale Umsetzung des bekannten Brettspiels, entwickelt im Rahmen eines Softwareprojekts an der IBS Oldenburg. Dieses Open-Source-Projekt besteht aus einem Java-Backend (Server) und einem Java-Client (Frontend). Die Anwendung kann lokal oder in Docker-Containern betrieben werden.

## Voraussetzungen

- [Java 17 oder höher](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/)
- (Optional) [Docker](https://www.docker.com/)

## Projektstruktur

- **server/** – Enthält das Backend
- **client/** – Enthält das Frontend

## Build & Start

### Client (Frontend)

**Bauen:**
```bash
cd client
mvn clean package
```

Das erzeugte JAR liegt unter `client/target`.

**Starten:**
```bash
cd client/target
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Server (Backend)

**Bauen:**
```bash
cd server
mvn clean package
```

Das erzeugte JAR liegt unter `server/target`.

**Starten:**
```bash
cd server/target
java -jar server-1.0.0-jar-with-dependencies.jar
```

## Nutzung mit IntelliJ IDEA

Konfiguriere eigene "Run Configurations", um Server und Client bequem zu starten:

1. Menü **Run** → **Edit Configurations...**
2. Mit "+" eine neue **JAR Application**-Konfiguration anlegen (Pfad zum jeweiligen JAR angeben)
3. Optional: Maven-Builds als "Before launch"-Aktion hinzufügen

## Konfiguration

- Im Projekt befindet sich eine `.env.example`-Datei
- Kopiere diese als `.env` und trage dort die erforderlichen Variablen ein

## Docker-Nutzung

### Server mit Docker bauen und starten

**Image bauen:**
```bash
cd server
mvn clean package
```

**Container starten:**
```bash
docker run -p 8899:8899 -d <image-tag>
```

Ersetze `<image-tag>` durch den Namen deines erstellten Images.

**Hinweis: Dies ist eine freie, nicht-kommerzielle Umsetzung des Spiels „Pandemie" zu Lernzwecken.**
