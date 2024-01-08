# Standardisiertes Logdatenformat

## Sammeln von Anforderungen bezüglich eines Logdatenformats

- Abwärtskompatibilität
- Einzellogs + Aggregierte Logdatenverarbeitung
- Erweiterbarkeit

        
## Konzeption des Datenformats für Einzelne Logdaten.

#### Vorschlag: Modellierung von Logdaten in Klassendiagrammen

Oberklasse für allgemeine Logs, basierend auf JSON-Format
```json
{
"timestamp": ...,
"level": ...,
"message": ...
}
```

dies erlaubt Erweiterungen, die von der Logdatenklasse erben, sodass 
unsere generierten Dser zum Einlesen der Logs verwendet werden können.

## Konzeption des Datenformats für aggregierte Logdaten:

Erweiterung der generierten Dser um JSON-Arrays einlesen zu können:
```json
[
  {
    "timestamp": ...,
    "level": ...,
    "message": ...
  },
  {
    "timestamp": ...,
    "level": ...,
    "message": ...
  },
  ...
]
```
Für Abwärtskompatibilität müssen entweder Logdaten in unser Standardformat adaptiert, 
oder handgeschriebene Dser genutzt werden.