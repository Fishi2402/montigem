# JSweet & MontiGem Besonderheiten: Das "Safe-Object" Architekturmuster

Um `NullPointerException` und `ClassCastException` während der JSweet-Transpilation (besonders bei komplexen Domänenobjekten) zu vermeiden, nutzt MontiGem häufig spezielle Architektur-Patterns. Diese sind essentiell, um Abstürze des JSweet-Transpilers oder Fehler zur Laufzeit im Browser zu verhindern.

## 1. Das "Safe-Object" Architekturmuster
*   **Async Loading:** Daten werden in `init()` asynchron geladen, der UI-Status wird über generierte `setVariable()` Methoden aktualisiert.
*   **List-Mapping:** `IUMLPList` Objekte werden innerhalb von `@Replace`-Blöcken mittels `.toArray()` in native JS-Arrays konvertiert, um Standard-Methoden wie `find()` oder `filter()` in TypeScript nutzen zu können.
*   **Typ-Entkopplung:** Komplexe Typen werden in `.gui`-Signaturen oft als generisches `Object` oder unspezifischer Typ geführt. Die Logik in Java-HWC Klassen arbeitet dann über `@Replace` sauber mit expliziten TypeScript-Casts, um Typprobleme beim Transpilieren zu umgehen.

## 2. JSweet-Schutz-Strategie & Limits (HWC)
Um Abstürze des JSweet-Transpilers zu verhindern:
*   **Empty Method Bodies:** Java-Methodenrümpfe in HWC-Klassen, die durch JSweet im Frontend genutzt werden, MÜSSEN leer sein (z.B. `return null;`), wenn sie in TypeScript durch `@Replace` ersetzt werden oder wenn sie intern zu komplex für JSweet sind.
*   **Logik-Delegation:** Komplexe Java-Logik für HWCs sollte in statische Hilfsmethoden innerhalb eines `internal`-Packages (z.B. `myproject.internal.*`) ausgelagert werden. **Dieses Package wird vom JSweet-Transpiler ignoriert.**
*   **Kein `var` bei Listen:** Vermeide `var` bei komplexen generierten Typen (insb. Listen), da JSweet bei der Typ-Inferenz scheitern kann.
*   **Kein `getLast()`:** Nutze `list.get(list.size() - 1)` anstelle von `list.getLast()` für MontiGem-Listen.
*   **Dateien komplett ausschließen:** Wenn Backend-Utility-Klassen im Frontend-Pfad liegen, schließe sie in der `build.gradle` aus:
    `tasks.named("umlpJava2ts") { excludeSource.add("glob:**/MyBackendOnlyUtil.java") }`
*   **Backend-Only Methoden:** Nutze `@BackendOnly(BackendOnlyModes.THROW_ERROR)` (oder `STATUS_OK` / `STATUS_ERROR` für Commands), damit JSweet die Methode im Frontend durch einen einfachen Error-Throw oder Result ersetzt.

## 3. Asynchronität & Imports in JSweet
*   **RTE Listen iterieren:** Die Iteration über `IUMLPList` ist asynchron. Nutze in TS: `for await (const item of myList)`.
*   **Assoziationen laden:** Getter für Assoziationen geben ein Promise zurück. Nutze `await` (z.B. `await myObject.getCorrespondsTo()`).
*   **@Async Annotation:** Jede Java-HWC Methode, die TS-Code mit `await` generiert (via `@Replace`), **muss** in Java mit `@jsweet.lang.Async` annotiert werden.
*   **Imports in @Replace:** Statische Imports für `@Replace` Blöcke müssen zwingend über `@Component(tsImports = {"MyService, from 'path/to/MyService'"})` deklariert werden.

## 4. Shadowing-Regel
Erstelle KEINE Java-HWC-Datei für Komponenten, die rein in TypeScript implementiert sind (z.B. `FileUpload.ts`). Die Java-Datei würde die TypeScript-Implementierung im Build-Prozess "beschatten" (shadowing) und deaktivieren, da der Transpiler die `.java` Datei nimmt und daraus eine neue `.ts` Datei generiert, welche die manuell erstellte `.ts` Datei überschreibt.

## 5. GUI-DSL Einschränkungen
*   **Parser Bugs:** Vermeide verschachtelte `@iterate` Blöcke innerhalb von `components = [...]`.
*   **Optional Handling:** Vermeide funktionale Operationen auf `Optional` (`map`, `flatMap`). Nutze klassische `if (opt.isPresent())` Checks in transpiliertem Code.