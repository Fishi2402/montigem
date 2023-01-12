# UMLPTool


## Use in Gradle
1. Add SE-Nexus to Plugin Repositories.

in ``settings.gradle``:
````
pluginManagement {
  repositories {
    maven { // For UMLP Plugin
      url = "https://nexus.se.rwth-aachen.de/content/groups/public"
    }
    gradlePluginPortal()
    mavenCentral()
  }
}
````

2. Load Plugin

at the top of ``build.gradle``:
````
plugins {
  id 'de.rwth.se.umlp-tool' version '7.5.0-SNAPSHOT'
  id 'java'
  // [...]
}
````

3. Add Dependencies of Generated Code in ``build.gradle``

````
repositories {
  maven {
    url "https://nexus.se.rwth-aachen.de/content/groups/public"
  }
}
dependencies {
  implementation group: 'umlp', name: 'umlp-rte', version: '7.5.0-SNAPSHOT'
}
````

4. Add Task in ``build.gradle``
````
task myFirstUMLPTask(type: UMLPTask) {
  input = file("models/")
  hwcDir = layout.projectDirectory.dir("hwc/")
  
  outputDir = layout.buildDirectory.dir("generated-sources/")
}
````

5. Add generated Code to Java-Input in ``build.gradle``
````
sourceSets {
  main.java.srcDirs += [generateStuff.outputDir]
}
````


### Configuration Options for ``UMLPTask``
| Option         | Type                   | Description                                                                                                           |
|----------------|------------------------|-----------------------------------------------------------------------------------------------------------------------|
| input          | File, Files, Directory | Collection of input models. Each file is a seperate generation process. Only `.umlp` and `.gui` models are processed. | 
| modelDir       | Directory              | Used to resolve import statements.                                                                                    | 
| hwcDir         | Directory              | Directory with Handwritten Code. Used for TOP-Mechanism.                                                              | 
| symbolDir      | Directory              |                                                                                                                       | 
| tmplDir        | Directory              | Location of custom template-files. Can be used to customize generation process.                                       | 
| configTemplate | String                 | Filename of `.ftl` file in `tmplDir`                                                                                  | 
| moreArgs       | String Array           | Not recommended! Can break incremental build! Allows to pass custom arguments to command line interface of UMLP.      | 
| debug          | Boolean                | Enable Debug output and disable Isolation (see below). Default: `false`                                               | 
| outputDir      | Directory              | Output for generated Files                                                                                            | 
| reportDir      | Directory              | Output for reports. Is used for Incremental Build. Default: `<buildDir>/umlp_reports/task_<taskname>`                 | 


### Debug ``UMLPTask``
By default, each run of an ``UMLPTask`` is isolated from its environment and debugging tools / breakpoints do not work.

When passing ``debug=true`` to an `UMLPTask` this isolation is disabled. Hence debugging is not compatible with executing multiple `UMLPTasks` in parallel.

To globally enable debugging for every ``UMLPTask``, set in `gradle.properties`:
````
de.rwth.se.umlp.debug=true
org.gradle.parallel=false  // parallel execution not compatible with debugging
````

Only enable the debug modus for debugging, and disable if afterwards. It must not be active for production builds.