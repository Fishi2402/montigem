<!-- (c) https://github.com/MontiCore/monticore -->
# UMLPTool

UMLP is designed to assist developers in generating web applications
from class diagrams,
simplifying the development process and accelerating project delivery.
The generated applications are ready to be deployed and can be customized further using handwritten
code to meet your specific requirements.

## Features

- Generate a Java Spring web application from class diagrams

## Installing

Make sure you have installed the following dependencies:
- Java 11 (check it with `java -version`)
- Gradle 7.6.4 (check it with `gradle --version`) older versions may lead to an error with the jackson dependency
- Make sure there is a file `~/.gradle/gradle.properties` with your (SE RWTH) Nexus username and password. Example:
```
mavenUser=johndoe
mavenPassword=mySuperStrongPassword
```

In case you already have different versions of Java / Gradle and do not want to switch completely,
we recommend [SDKMan](https://sdkman.io/) for managing different versions on the same computer.

After installing the dependencies, run `gradle build` in the top folder of the project.

## Quick Start

You can try UMLP using the [SELib Example](./examples/selib).

- In a terminal window, go to the `examples/selib` folder and run `gradle runServer`, which will (deliberately) not finish
- Once you see the following log message, you can continue: `[INFO]  UMLP API Status: RUNNING`
- Open `http://localhost:8081/selib/` in your browser
- In your browser you can try to add new data to the tables
- Once you're finished you can stop the server in the terminal window by pressing the `CTRL` and `C` keys

## Editor plugins
- Download Latest Plugin
  - [IntelliJ](https://git.rwth-aachen.de/monticore/umlp/-/jobs/artifacts/dev/raw/umlp-tool/build/distributions/umlp-tool-7.7.0-SNAPSHOT.zip?job=build_with_containers)
  - [Visual Studio Code](https://git.rwth-aachen.de/monticore/umlp/-/jobs/artifacts/dev/raw/umlp-tool/build/generated-sources/UMLPWithGUI/plugins/umlpwithgui-vscode-plugin/umlpwithgui-lsp-7.7.0-SNAPSHOT.vsix?job=build_with_containers)
- Install it
  - IntelliJ: https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk
  - VSCode: https://code.visualstudio.com/docs/editor/extension-marketplace#_install-from-a-vsix

## Project Structure
* **doc** contains the documentation for the UMLPTool
* **examples** contains examples that demonstrate commonly used features of the UMLPTool
* **experiments** contains experiments that demonstrate edge cases of specific UMLPTool features and, at the same time, are integration tests for the UMLPTool
* **umlp-rte** contains the implementation of the run-time environment, i.e., the code that is the same for all applications
* **umlp-tool** contains the implementation of the language, tool, and generator
