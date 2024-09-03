<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Relevant Publications -->
[LRRS23]: https://www.se-rwth.de/publications/CDMerge-Semantically-Sound-Merging-of-Class-Diagrams-for-Software-Component-Integration.pdf

<!-- Other Links -->
[cd4analysis]: https://github.com/MontiCore/cd4analysis
[Car Rental]: https://git.rwth-aachen.de/monticore/umlp-anwendungen/car-rental
[selib]: ../examples/selib

# CDMerge
The UML/P tool's primary function is the partial generation of an application
from a domain model.
To allow for a more modular approach to domain modeling, the merge operator 
CDMerge can be used to compose multiple class diagrams modeling specific parts
or aspects of the system into a complete domain model on demand.
The operator is described in **[LRRS23]** and its implementation can be found in 
the **[cd4analysis]** project.

Click [here](https://www.monticore.de/download/MCCD.jar) to download the CD Tool
JAR containing the CDMerge implementation.

## Gradle Task
For convenient usage of CDMerge, the UMLP to provides the gradle task 
`UMLPMergeTask`. This task parses the specified input-files as class
diagrams, applies the merging operator CDMerge, and prints the composed
domain model to a specified model-file in the build-directory.

### Input configuration
* source: the source-files or directory containing the source-files
* includes: specifies the files in the source directory to be considered 
  as source files (optional)
* excludes: specifies the files in the source directory NOT to be considered
  as source files (optional)
* outputFile: the file containing the composed model

### Output
* A merge of the input-models from the specified source-files in the specified
  output-file.

### Usage 
The task can be incorporated in combination with the backend generation step of
the product.

```
tasks.register('mergeUMLPModels', UMLPMergeTask){
  source = files("../models/$CD1.umlp", "../models/$CD2.umlp")
  outputFile = layout.buildDirectory.file("$MergedCD.umlp")
}
```

or, alternatively:

```
tasks.register('mergeUMLPModels', UMLPMergeTask){
  source = layout.projectDirectory.dir("../models/")
  includes = ["**.umlp"]
  excludes = ["$ExcludedModel.umlp", "**.gui"]
  outputFile = layout.buildDirectory.file("$MergedCD.umlp")
}
```
Note that if you want to utilize the symbols of the merged model in the 
frontend of your application, you have to make corresponding adjustments to 
the build-script of the frontend, as well.

A small example can be found in the **[selib]** subproject.
For a larger example refer to the **[Car Rental]** project.

## CLI Command

CDMerge can also be executed via CLI with the `UMLPTool`. 
By using the option `-mrg` or `--merge` one can specify multiple files
containing class diagrams. These will be merged with the input model specified
via `-i` or `--input`. If the merge is successful, the merged CD replaces the
original input CD in memory. Further operations will therefore be executed
on the merged model, e.g.:

```
java -jar $dir/UMLPTool.jar -i $srcDir/$CD1.umlp -mrg $srcDir/$CD2.umlp, $srcDir/$CD3.umlp -pp $tgtDir/$MergedCD.umlp 
```
