<!-- (c) https://github.com/MontiCore/monticore -->

# POJO Generator

The Pojo Generator produces light-weight data classes, which shall not be persisted. 
Thus, it only produces the data classes and the minimal viable infrastructure necessary.
Its intentional use is generating the required infrastructure for instantiating and transporting 
carrier objects for non-persistent data. Additionally, the synthesized data types can be employed 
for defining types exclusively required for GUI models, i.e., types necessary for 
front-end representation, which are not part of the overall data structure.

## Current State
The Pojo Generator generates Data Classes, Builders and DeSerializers for each class in a CD. 
It enables the light-weight generation of data aggregates and their communication infrastructure 
with the front-end. In contrast to the normal UMLP Generator, the generated data classes do not use 
GemLists, because we specifically do not want lazy loading for these objects. 


### Usage

To incorporate the generator in your own gradle project, you can use the GUITypes2JavaTask with the
following input configurations.

**Input configuration**

* input: The input CD model describing the POJO data types
* outputDir: The directory the generated classes will be located
* hwcDir: The path to hand-written artifacts, enabling the TOP mechanism

Example task configuration task from `build.gradle` of `umlp-rte`: 

```
tasks.register('generateGuiComponentJava', GUITypes2JavaTask) {
  hwcDir = project.layout.files("src/main/java")
  input = file("client/models/")
  outputDir = project.layout.buildDirectory.dir("guiComponentTypes")
}
```

### Current Application

As of now, the generator is used for class diagrams in `umlp-rte/client/models` where the generator
is executed in the `generateGuiComponentJava` Gradle task.
To integrate the DeSer, for each diagram a DeSer class is generated which contains a map of all 
available DeSers in the CD. This map has to be added to an instantiated BaseClassObjectMapper for 
the DeSerialization to properly work.

Because we want to be able to use these types client and server side we generate them for Java 
and in the near future plan to transpile them with JSweet to be able to use them in JS as well. 

## Future:
- collect other Use-Cases for the generator to decide if the current one fits all the needs
  - generation of Chart types to efficiently visualize large data without sending everything
  - generator for objects to use as input/output in algorithms which should not be stored in the 
    database but can remain in a server cache e.g. Session specific stuff  
- enable pojo generation of classes defined in domain models
  - decide on syntax   
