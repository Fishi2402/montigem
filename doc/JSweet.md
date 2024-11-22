# JSweet
The transpiling of our java code to Typescript code

## Setup
Um JSweet in ein Projekt zu integrieren, müssen diese Schritte befolgt werden

### Backend Projekt
Das Backend Projekt dient als Ausgangsquelle für die Java Sources der Domain Quellen
und muss entsprechend konfiguriert werden
1. Das `java-library` Plugin aktivieren
```groovy
plugins {
  id 'java-library'
}
```

2. Dependencies auf api umschalten für
    - `umlp-rte`
    - `umlp-test-rte`
    - `spring-boot-starter-web`
    - `spring-boot-starter-websocket`
    - `spring-boot-starter-data-rest`
    - `spring-boot-starter-test`
    - `com.h2database:h2:2.1.214`
```groovy
dependencies {
  api group: 'umlp', name: 'umlp-rte', version: mc_version
}
```

### Frontend Projekt
1. In der Datei `gradle.properties` (anlegen, falls nicht vorhanden)
den Wert `j2ts` auf `true` setzen und den Wert `domainModel` entsprechend setzen  
z.B. (Anführungszeichen sind optional)
```groovy
j2ts = true
domainModel = 'FunctionStructure4Fenix'
```

2. In der Datei `build.gradle` im `dependency` Block eine 
`implementation` sowie eine `domainSources` Abhängigkeit auf das `backend` 
Projekt anlegen  
z.B. 
```groovy
dependencies {
    domainSources project(":backend")
    implementation project(":backend")
}
```

3. `app.component` anpassen
    - imports aus `commonj2ts`
      ```typescript
      import {
        ClientCommandManager,
        ClientCommandWebsocketCommunication,
        CommandManager
      } from '@umlp/commonj2ts';
      ```
    - Initialisieren des `ComponentObserverManager`
      ```typescript
      constructor() {
        ...
        FunctionStructure4FenixComponentObserverManager.init();
      }
      ```

## Entwickeln
Nun kann im Frontend Projekt in `src/main/java` per Top Mechanismus der 
generierte Code aus GUI Modellen angepasst werden. GUI Komponenten
müssen mit `@umlp.jsweet.extension.annotation.Component` annotiert werden.
Diese kann entsprechend konfiguriert werden
```java
public @interface Component {
  boolean standalone() default true;
  String[] imports() default { "...config.imports" };
  String templateUrl() default "config.templateUrl";
  String[] styleUrls() default { };
  String styles() default "config.styles";
  String selector() default "config.selector";
  String cdName() default "";
  String[] tsImports() default {};
}
```

Sollte es doch notwendig
sein, den Typescript Code direkt anzupassen, kann dies momentan umgesetzt 
werden, indem entsprechende Datei mit gleichem Orderpfad unter
`src/main/ts/gui` bzw. `src/main/ts/domain` erstellt wird. Diese überschreiben
dann alle transpilierten Dateien mit gleichem Namen.

### Nicht unterstützte Features
Die Nutzung von JSweet schränkt den Funktionsumfang von Java an einigen 
Stellen ein.
* Die Nutzung von `var` zur Variablendeklaration wird nicht unterstützt
* Alle `Stream` Operationen werden nicht unterstützt
* In Gui Komponenten darf der Konstruktor nicht überschrieben werden

## Anpassbarkeit
An einigen Stellen, kann das Plugin sowie die Transpilierung konfiguriert werden

### Plugin
Folgende Pfade können in der Datei `gradle.properties` gesetzt werden
(inkl. defaults)
```groovy
# ${projectDir}/src/main/java
jsweetjavaHWCPath = "src/main/java"
# ${buildDir}/jsweet
jsweetBuildDirBase = "jsweet"
# ${buildDir}/umlp_frontend/intermediate/appdomain
jsweetDomainOutDir = "umlp_frontend/intermediate/appdomain"
# ${buildDir}/umlp_frontend/intermediate/appguiJ2TS
jsweetGuiOutDir = "umlp_frontend/intermediate/appguiJ2TS"
# ${buildDir}/umlp_frontend/intermediate/appguiJ2TSconfig
jsweetGuiAdditionalsOutDir = "umlp_frontend/intermediate/appguiJ2TSconfig"
# ${projectDir}/jsweet/domain
jsweetDomainConfigHWCDir = "jsweet/domain"
# ${projectDir}/jsweet/gui
jsweetGuiConfigHWCDir = "jsweet/gui"
# ${jsweetBuildDirBase}/configGen/domain
jsweetDomainConfigOutDir = "configGen/domain"
# ${jsweetBuildDirBase}/configGen/gui
jsweetGuiConfigOutDir = "configGen/gui"
# ${projectDir}/src/main/ts/domain
jsweetTSDomainHWCPath = "src/main/ts/domain"
# ${projectDir}/src/main/ts/gui
jsweetTSGuiHWCPath = "src/main/ts/gui"
```

### Transpilierung

#### JSweet Config
Der Ausgangspunkt für die Konfiguration der Transpilierung ist die JSON 
Datei `jsweetconfig.json`. Diese wird generiert und sieht standardmäßig z.B. so aus
```json
{
  "adapters": [
    "umlp.jsweet.extension.UMLPJSweetDomainAdapter",
    "umlp.jsweet.extension.domainAdapters.FunctionStructure4FenixImportAdapter"
  ],
  "domainName": "FunctionStructure4Fenix"
}
```
Im Teil `"adapters"` können die verschiedenen Adapter gesetzt werden.
Im Teil `"domainName"` wird entsprechender Name des Klassendiagramms gesetzt.
Zusätzlich gibt es noch diese Möglichkeiten (mehr dazu in der [JSweet Doku](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md#centralizing-annotations-in-jsweetconfigjson)) 
* `Erased` um Elemente nicht zu übersetzen
* `Name` um Elemente umzubenennen
* `Replace` um Methodenrümpfe zu ersetzen

Diese werden dann über glob Expressions gesetzt (mehr dazu in der [JSweet Doku](Mehr dazu in der [JSweet Doku](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md#centralizing-annotations-in-jsweetconfigjson)))
z.B. 
```json
  "@Erased": {
    "include": [
      "**.spliterator*()",
      "**.stream*()",
      "**.parallelStream*()"
    ]
  }
```

#### Adapter
Adapter können die Transpilierung grundlegend verändern. Zahlreiche Beispiele 
finden sich im Projekt `:umlp:umlp-rte:umlp-jsweet`

## TypeScript Code einbinden
Manchmal ist es hilfreich TypeScript Code direkt einzubinden, z.B. wenn eine TS Library wiederverwendet werden soll(z.B. für highlighting von Code Blöcken).

**ACHTUNG: Der entstehende Code ist nicht in Java testbar, daher sollte kein Verhalten so implementiert werden!**

Als erstes muss in Java die Signatur in einem Subpackage von `def` angelegt werden:

`frontend/src/main/java/def/foo/MyUtil.java`
```
public class MyUtil {
  public native void foo();
}
```
darauf hin erstellt JSweet eine entsprechene `d.ts` Header Datei im generierten Code.

Dann wird die Methode in TS implementiert:
`frontend/src/main/ts/gui/def/foo/MyUtil.ts`
```
export class MyUtil{
  public foo(){
    console.log("Hello World!");
  }
}

```

Als letztes muss in Java der nutzenden Komponente ein import hinzugefügt werden und der Code wird aufgerufen:

```
@Component(
  tsImports = {"MyUtil, @src/def/foo/MyUtil"}
)
public class MyPage{
  @Replace("this._bar();")
  public void bar(){
    System.out.println("Mock of MyUtil#foo");
  }

  public void _bar(){
    new MyUtil().foo();
  }
}
```

Durch die Indirektion und die `@Replace` Annotation ist die Komponente weiterhin in Java testbar, ohne das `foo` ausgeführt wird.
