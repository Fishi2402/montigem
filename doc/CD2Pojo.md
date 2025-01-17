

````mermaid
flowchart LR
  ast[Load origin AST]
  transform[Transform origin CD-AST]
  ast-->transform
  create[Create a bare derived CD]
  transform-->create
  decorate[Decorate the derived CD]
  create-->decorate
  generate[Write the derived CD to files]
  decorate-->generate
````

We start by loading the origin CD-AST (e.g., by parsing it),
 followed by optionally transforming the AST itself.
For examples,
 associations to primitive types may be transformed to attributes.
Next, decorators add features to a 2nd, derived CD based on a 
 configuration and the original CD.
The order of decorators CAN be important, e.g., the TOP decorator 
 modified previously created&decorated classes.
Finally, we output ("generate") the classes to files.
 


With builders:
  Require the name of the derived data class?



## Decorators
* Data class
* Getter
* Setter
* toString, equals, ..
* Builder
* Constructor
* MontiGem Builder (ABuilder extends A)
* Visitor
* Observeable
* (MontiGem data classes)
* TOP Decorator: Rename class F to FTOP iff F.ext file exists

Some decorators require information,
 such as the name of the data class, etc.

Other decorators can depend on others being run too,
 e.g., Observeables require Setters,
 and even extend the decorated Setters.

Interesting idea: It might be possible to run decorators "in parallel", 
 i.e. during the same CD-visitation.

## Accessibility 
This tooling has to be provided as a simple gradle plugin, as a standalone CLI-jar,
 and as a library for inclusion.
All distributions must be configurable.

Import symbols & export generated symbols. 

## Configuration

We have multiple ways of configuring our tool:
 * Stereotypes
 * CLI arguments / Gradle task properties
 * external symbol information (tagging)
 * ConfigTemplate

```cd
<<withbuilders>>
classdiagram MyCD {
```

```cd
classdiagram MyCD {
  <<withbuilder>> class MyCD;
```

Defaults should be provided via a config template (or similar mechanism).
Depending on where we aim to use the CD-Generator,
 different defaults shall be assumed (MontiGem vs POJO use case).


