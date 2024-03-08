# Function Structure Model

## Purpose
FunctionStructure describes the functional structure of machines, products, and
distributed software

The function of a system (or component) is described
- in the form of input and out channels
- with a concrete, mostly typed signature
- energy, physical, or data streams flow on the channels; these can be 
  understood (and modeled) as discrete events (button, screw) or continuous 
  flows (water, electricity, heat)

Functions
- are decomposed into substructures (architectures), which in turn 
  interact via internal channels
- can have an internal state (and change to through interactions with 
  the environment)
- can have complex properties, but ultimately break down to either 
  pure software, sensors, actuators, or elementary functions, as the Koller 
  catalog knows them in the form of about 300 pieces

## Explanation
There is a thorough explanation [here](doc/FunctionStructure.Explanation.pptx)

## The model
- primarily defined by a [class diagram](src/models/FunctionStructure.umlp)
- and [ocl constraints](src/models/FunctionStructure.ocl)
- incarnated with a [class diagram for implementation purposes](src/models/FunctionStructure4Fenix.umlp) in Fenix
- and corresponding [ocl constraints](src/models/FunctionStructure4Fenix.ocl)

## Application
We collect possible use cases for which our function structure model will be
applied. In most cases, it acts as an intermediate structure to capture the
structural elements of a functional composition of a system, and then 
transform it further. In other cases, it acts as the source for analysis
or data structure directly.

These concepts are also collected [here](doc/FunctionStructure.UseCases.pptx).

### Digital Twin Cockpit
- We can use the functional structure of a system to derive visualizations
  to be used in a digital twin cockpit.
- We store all information in a database, conforming to the function
  structure model, in Fenix, our generator framework for information systems.
- We define standard GUI components which visualize
  - all components of the system (functions, subfunctions, ports, and channels)
    in a hierarchical composed containment tree
  - a graphical notion of component \& connector diagrams
  - values on channels
![img.png](img/DTCockpit01.png)

### Abstraction of component \& connector languages
- There are different languages which contain similar concepts regarding
  the functional structure of systems as connectors and components
  - E.g., MontiArc, SysMLv1, SysMLv2, Simulink, LabView
- The funtion structure model can act as an abstraction from these languages
  and store information in a common data structure
![img.png](img/CnCAbstraction.png)
