# Prerequisites

This document describes what you need to install in order to run a MontiGem app,
and commands defined for a standard app setup.

## Installation

### Install Java 21

!!! note
    The following are the instructions for Windows. For other systems look it up on the internet.

- Download and install [java 21 archive](https://www.oracle.com/java/technologies/downloads/#jdk21-windows) and extract the folder. Alternatively look for `21` version of Java in the Java archive.
- Extracted folder will contain `jdk-21`. You can copy it into `Program Files/Java` if you want, it is a standard path for JDK.
- Add environment variable `JAVA_HOME` with its value set to the `jdk-21` directory, e.g. `C:\Program Files\Java\jdk-21`.
- Add `%JAVA_HOME%\bin` entry to the `Path` environment variable. Open a command line and run `java -version` command to check if Java is working properly.

### Setup Gradle 8.5

Download [Gradle 8.5](https://gradle.org/next-steps/?version=8.14.3&format=bin) and do an installation according to ["Installing manually" section](https://gradle.org/install/).

Create a `gradle.properties` file in `%userprofile%/.gradle` folder with content:

=== "gradle.properties"
    ```
    mavenUser=my_user
    mavenPassword=my_password
    ```
  
Replace `my_user` and `my_password` with your Nexus account name and password.

(Optional) Install a plugin for language support:

**Downloads**

  - [IntelliJ](https://git.rwth-aachen.de/monticore/umlp/-/jobs/artifacts/dev/raw/umlp-tool/umlp-intellij-plugin/build/distributions/umlp-intellij-plugin-7.9.0-SNAPSHOT.zip?job=build_intellij_plugin)
  - [Visual Studio Code](https://git.rwth-aachen.de/monticore/umlp/-/jobs/artifacts/dev/raw/umlp-tool/build/generated-sources/UMLPWithGUI/plugins/umlpwithgui-vscode-plugin/umlpwithgui-lsp-7.9.0-SNAPSHOT.vsix?job=build_with_deploy)

**Plugin installation guides**:

  - IntelliJ: [https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk)
  - VSCode: [https://code.visualstudio.com/docs/editor/extension-marketplace#_install-from-a-vsix](https://code.visualstudio.com/docs/editor/extension-marketplace#_install-from-a-vsix)

## Commands
Clone the project and build it.

### Build

To build the project, use the following command:
```sh
gradle build
```
  
### Run

Run the project's backend and frontend using following commands (each command will not stop on its own, since you are starting continous processes):

Open a console/terminal and start backend:

```sh
gradle :backend:run
```

If there are no errors in the output, the last lines will signal the
successful backend start (there may be more output after these lines):

```
[main] INFO domain.DomainServerApplication - Started DomainServerApplication in ... seconds (JVM running for ...)
shell:>
```

Open another console/terminal and start frontend:

```sh
gradle :frontend:run
```

If there are no errors in the ouput, the last lines will signal the
successful frontend start (there may be more output after these lines):

```
** Angular Live Development Server is listening on localhost:4200, open your browser on http://localhost:4200/ **

... Compiled successfully.
```

Open the browser and navigate to [localhost:4200](http://localhost:4200) to see the app.

### Stop

Stop the backend or frontend by pressing `ctrl+c` in the console/terminal.

!!! warning
    Next instruction kills a process running on port 4200. This is the
    node process running frontend app. If you have a special setup that uses
    port 4200 for something else you will need to adjust the scripts.

Run the `killfe` script in the project folder in a console with administrative
rights:

```
killfe
```

or for Unix-like environment

```
sudo ./killfe.sh
```

!!! note
    `killfe` stands for kill frontend. The script finds the process which
    uses port 4200 and kills it if such process is found. This is a crutch of
    `gradle` build system/OS process and thread management. Internally the
    `gradle :frontend:run` command starts a dev-server for the angular app. The
    server runs in the separate process, not controlled by gradle. The behavior
    may vary and you might not need to kill the process manually/it might
    terminate on its own later.

### Good to know

```sh
  gradle :frontend:run
```

Starts a local node server. The startup time is quite slow. If you are only 
changing FrontEnd code or GUI models, you can simply let the taks run and 
instead use the following task to serve the new code in much less time.

```sh
  gradle :frontend:SYNC_genGUI
```

!!! note
    This command is currently outdated and will be reworked.
