# MontiGem Patterns & Pitfalls

Dieses Dokument beschreibt häufige Fehler bei der Entwicklung mit MontiGem/JSweet und die daraus resultierenden Best-Practice-Patterns.

---

## 1. GUI Modeling & Layout Patterns

### 1.1 Komplexe Logik in Guards
*   ❌ **Fehleranfälliger Weg:** Komplexe Java-Ausdrücke direkt im `@guard`: `@guard(p.status == ACTIVE && searchTerm != null)`.
*   ✅ **Der MontiGem-Weg:** Die Logik in eine Java-Methode auslagern: `@guard(isVisible(p, searchTerm))`.

### 1.2 String-Konkatenation in Argumenten
*   ❌ **Fehleranfälliger Weg:** Strings in der GUI-DSL addieren: `@GemText(value = "Status: " + p.status)`.
*   ✅ **Der MontiGem-Weg:** Eine "String Factory" Methode in Java erstellen: `@GemText(value = getStatusLabel(p))`.

### 1.3 Semikolons & Kommas in Component Lists
*   ❌ **Fehleranfälliger Weg:** Fehlende Trennzeichen nach `@guard` oder `@iterate` innerhalb von `components = [...]`.
*   ✅ **Der MontiGem-Weg:** Beende Statements im Block mit `;` und trenne die Blöcke im Array mit `,`:
    ```java
    components = [
        @guard(cond) { @GemText(...); }, // Komma als Trenner!
        @iterate(item : list) { @GemText(...); }
    ]
    ```

### 1.4 Korrekte Verwendung von GemDialog
*   ❌ **Fehleranfälliger Weg:** Den Dialog via `@guard` komplett aus dem DOM entfernen (z.B. `@guard(isOpen) { @GemDialog(...) }`).
*   ✅ **Der MontiGem-Weg:** Einen Boolean-Parameter an die Page übergeben und an `isOpen` binden: `myDialog@GemDialog(isOpen = isMyDialogOpen)`. Event-Handler steuern dann das Feld `this.setIsMyDialogOpen(true)`.

### 1.5 Flexbox & Height 0
*   ❌ **Fehleranfälliger Weg:** `height: 100%` auf ein Kind setzen, wenn der Parent keine explizite Höhe hat (Element kollabiert).
*   ✅ **Der MontiGem-Weg:** Sicherstellen, dass die Parent-Chain Höhen hat, oder Fallback nutzen: `min-height: 400px;`.

---

## 2. Java HWC & JSweet Patterns

### 2.1 Boolean Getters
*   ❌ **Fehleranfälliger Weg:** Für das Attribut `boolean isSelected` den Getter `getIsSelected()` aufrufen.
*   ✅ **Der MontiGem-Weg:** MontiCore generiert standardmäßig `isIsSelected()`.

### 2.2 Package Naming
*   ❌ **Fehleranfälliger Weg:** Das kapitalisierte CD-Modell als Java-Package nutzen (`import Dtmm.Assessment;`).
*   ✅ **Der MontiGem-Weg:** Java-Packages sind immer kleingeschrieben (`import dtmm.Assessment;`). Nur in der `.gui` wird direkt auf das Modell referenziert (`Dtmm.Assessment`).

### 2.3 Primitive vs. Wrapper Return Types
*   ❌ **Fehleranfälliger Weg:** Eine TOP-Methode überschreiben und `boolean` zurückgeben, obwohl die TOP-Klasse `java.lang.Boolean` erwartet.
*   ✅ **Der MontiGem-Weg:** Immer Wrapper-Klassen (`Boolean`, `Integer`) für HWC-Rückgaben an die GUI nutzen.

### 2.4 Aufruf von Singleton-Befehlen aus dem Frontend
*   ❌ **Fehleranfälliger Weg:** `App.getInstance().myMethod()` aus dem Frontend-HWC aufrufen.
*   ✅ **Der MontiGem-Weg:** Singleton-Methoden werden zu Commands. Nutze den generierten statischen Aufruf: `App.executeMyMethod(param);`.

### 2.5 Das "List-Item-State" Problem
*   ❌ **Fehleranfälliger Weg:** UI-Zustand (z.B. Dropdown-Auswahl in einer Liste) in einer Page-Variable speichern. Ändert man eins, ändern sich alle.
*   ✅ **Der MontiGem-Weg:** Den UI-Zustand in das Domänenobjekt (CD) als `temp...` Feld aufnehmen: `class Task { String tempStatus; }`.

### 2.6 Manuelle Listen-Verwaltung
*   ❌ **Fehleranfälliger Weg:** Liste manuell via `SEOrgaManager.getProjectList()` laden und speichern. Es findet keine Live-Synchronisierung statt.
*   ✅ **Der MontiGem-Weg:** Die Liste als Parameter in den Page-Header der `.gui` aufnehmen. MontiGem initialisiert und synchronisiert sie automatisch.

### 2.7 Java Streams und JSweet
*   ❌ **Fehleranfälliger Weg:** `myGemList.stream().filter(...).collect(...)`. (Gibt JS-Runtime-Fehler).
*   ✅ **Der MontiGem-Weg:** Klassische `for`-Schleifen verwenden.

### 2.8 Mandatory Felder in Buildern
*   ❌ **Fehleranfälliger Weg:** Objekt via `builder().build().get()` erzeugen und Pflichtfelder danach setzen. Führt oft sofort zu Validierungsfehlern beim Build.
*   ✅ **Der MontiGem-Weg:** Alle Pflichtfelder direkt in die Builder-Chain packen.

---

## 3. Architektur & Performance Patterns

### 3.1 Manager-Architektur (TOP vs. Impl)
*   ❌ **Fehleranfälliger Weg:** Methoden direkt im HWC Manager überschreiben und vermischen.
*   ✅ **Der MontiGem-Weg:** Die Impl-Klasse mit `@jsweet.lang.Erased` markieren und Backend-Methoden mit `@BackendOnly` versehen.

### 3.2 Responsive Charts (ResizeObserver)
*   ❌ **Fehleranfälliger Weg:** Chart-Größe nur in `ngOnInit` berechnen. (Charts verschwinden bei Layout-Änderungen).
*   ✅ **Der MontiGem-Weg:** In TypeScript einen `ResizeObserver` auf den Container setzen, um bei Änderungen (z.B. Sidebar Toggle) den Chart neu zu zeichnen.