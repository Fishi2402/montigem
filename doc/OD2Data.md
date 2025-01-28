# OD2Data

The UMPL tool provides means to use object diagrams 
(ODs) to model test or initial data for the generated 
product.
The input must adhere to the data structures defined 
by the domain model (i.e., class diagram).

## Gradle Task
For convenient usage, the UMLP to provides the gradle 
tasks `UMLPOD2DataTask`. The task generates an 
infrastructure for instantiating the modeled object
structure. 
This comprises an `Instantiator` class, which, when 
called via its `instantiate` method, create the 
corresponding object instances. 
It also returns these objects. 
However, as the UMLP infrastructure typically 
automatically adds them to the database when created,
it usually suffices instantiating the object structure
in this context.

### Input configuration
* input: The input OD model describing the data to be instantiated
* outputDir: The directory the generated instantiator will be located
* symbolDir: The path to the directory in which the symbol table of the domain model is located
* symbolOutDir: The path to the directory in which the symbol table of the object diagram shall be stored (optional)
* hwcDir: The path to hand-written artifacts, enabling the TOP mechanism

### Output
* Instantiator class: A class for instantiating the desred object structure.
  * Naming convention: `*ODName*` + `Instantiator.java` 

## Usage
The task can be incorporated in combination with the 
backend generation step of the product. 

```
task genTestData(type: UMLPOD2DataTask) {
  dependsOn(generateUmlpBackend)
  input = file("../models/Example.od")
  outputDir = file("$project.buildDir/testdata")
  hwcDir = file("$projectDir/src/main/java")
  symbolDir = files(generateUmlpBackend.symbolOutDir)
  symbolOutDir = file("$project.buildDir/testdata/symtab")
}

sourceSets {
  main {
    java.srcDirs += [genTestData.outputDir]
  }
}

```

An example can be found in the kitchen companion project:
* The [object diagram](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/examples/kitchencompanion/models/Example.od?ref_type=heads)
* Using the [OD2Data task](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/examples/kitchencompanion/backend/build.gradle?ref_type=heads#L104-110)
* Calling the [generated instantiator](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/examples/kitchencompanion/backend/src/main/java/kitchencompanion/KitchenCompanionServerApplication.java?ref_type=heads#L388)
