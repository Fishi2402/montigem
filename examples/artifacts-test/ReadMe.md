<!-- (c) https://github.com/MontiCore/monticore -->
Dies ist ein Testprojekt, um zu prüfen, ob die derzeitige Implementierung des UMLP Generators (inklusive DB Anbindung 
zu einer Neo4j Datenbank) ausreicht, um Artifact Tooling darauf aufzubauen.

### Schritte um die Anwendung zu starten: 

1. Neo4j for Desktop runterladen und starten: https://neo4j.com/
2. Neues Projekt in Neo4j erstellen. Addresse und Credentials in src/main/resources/application.properties hinterlegen
3. Neo4j Datenbank starten
4. Neo4jApplication (src/main/java/baseartifactstest/Neo4jApplication) starten

### Fehlende Schritte bis UMLP in Artifact Tooling benutzt werden kann / ToDos: 

- Ordentliche Testumgebung für Unittests mit Neo4J Datenbank aufbauen.  
  - dazu Neo4j DB aus Java starten 
- GemList richtig (in Datenbank) persistieren und laden können
  - hierzu hilft / muss geklärt werden wie das bei relationalen DBs funktioniert (zurzeit gar nicht)
- Annotationen richtig generieren (vor allem Assoziationen werdeb zurzeit noch falsch generiert werden) 
