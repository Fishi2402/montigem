<!-- (c) https://github.com/MontiCore/monticore -->

**Currently** The Pojo Generator generates Data Classes, Builders and DeSerializers for each class in a CD. 
In contrast to the normal UMLP Generator, the generated data classes do not use GemLists.

As of now the generator is used for class diagrams in `umlp-rte/client/models` where the generator
is executed in the `generateGuiComponentJava` Gradle task.
To integrate the DeSer, for each diagram a DeSer class is generated which contains a map of all available
DeSers in the CD. This map has to be added to an instantiated BaseClassObjectMapper for the DeSerialization 
to properly work.

Because we want to be able to use these types client and server side we generate them for Java 
and in the near future plan to transpile them with JSweet to be able to use them in JS as well. 

# Future:
- collect other Use-Cases for the generator to decide if the current one fits all the needs
  - generation of Chart types to efficiently visualize large data without sending everything
  - generator for objects to use as input/output in algorithms which should not be stored in the 
    database but can remain in a server cache e.g. Session specific stuff  
- enable pojo generation of classes defined in domain models
  - decide on syntax   
