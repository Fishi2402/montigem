<!-- (c) https://github.com/MontiCore/monticore -->

The GUI-RTE defines GUI Component which additionally may receive specific data types as input.
The data types are defined in a separate class diagram which has to be imported by the GUI component.

Because we want to be able to use these data types client and server side we generate them in Java 
and in the near future plan to transpile them with JSweet. 

As of now all class diagrams in `umlp-rte/client/models` are used as input for the generator which 
is executed in the `generateGuiComponentJava` Gradle task. 
For each class in a CD 3 java classes are being generated: data class, builder class and DeSer class.
In contrast to domain classes, the generated data classes do not use GemLists.
Furthermore, for each diagram a DeSer class is generated which contains a DeSer map. The DeSer map
has to be added to an instantiated BaseClassObjectMapper for the DeSerialization to properly work.

