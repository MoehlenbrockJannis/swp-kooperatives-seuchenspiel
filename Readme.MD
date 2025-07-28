**Kooperatives Seuchenspiel** ist eine digitale Eigenentwicklung, die sich lose am bekannten Brettspiel „Pandemie“ orientiert. Das Spiel wurde im Rahmen eines Softwareprojekts an der IBS Oldenburg entwickelt und besteht aus einem Java-Backend (Server) sowie einem Java-Client (Frontend). Die Anwendung ist sowohl lokal als auch in Docker-Containern ausführbar.

> **Hinweis:**  
> Dieses Open-Source-Projekt steht in keinerlei Verbindung zu Z-Man Games, Asmodee oder den offiziellen Machern von „Pandemie“.  
> Es dient ausschließlich zu Lern- und Demonstrationszwecken.  
> Sowohl Regelwerk als auch grafische Elemente wurden eigenständig umgesetzt und weichen teils deutlich vom Original ab.

## Abweichungen von den Originalregeln („Pandemic“/„Pandemie“)

Im Vergleich zum originalen Brettspiel gibt es im hier umgesetzten Spiel folgende Änderungen und Abweichungen:

- **Bestimmung des Startspielers:**  
  Im Original beginnt die Person, die zuletzt krank war.  
  → In der digitalen Version wird der Startspieler zufällig bestimmt.

- **Zuordnung der Rollen:**  
  Im Original werden Rollenkarten gezogen und zufällig verteilt.  
  → In dieser Umsetzung kann jede*r Spieler*in die gewünschte Rolle in der Lobby selbst wählen. Wurde keine Rolle gewählt, erfolgt eine zufällige Zuweisung.

- **Übersichtskarten und Anleitung:**  
  Im Original dienen Übersichtskarten nur der Erklärung möglicher Aktionen.  
  → Die Umsetzung enthält eine umfassende, digitale Anleitung, die über die Aktionsübersicht hinaus auch alle Komponenten, Rollen und den Spielablauf erläutert.

- **Seuchen-Ausrottung nach Gegenmittel:**  
  Im Original können nach Entwicklung eines Gegenmittels weiterhin Seuchenwürfel der entsprechenden Farbe verteilt werden, solange die Krankheit nicht komplett ausgerottet ist.  
  → In der Software werden keine Seuchenwürfel mehr platziert, nachdem das Gegenmittel für eine Farbe entdeckt wurde (Vereinfachung der Regeln).

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
