<!-- (c) https://github.com/MontiCore/monticore -->
# UMLPTool Project

## Project Structure
* **doc** contains the documentation for the UMLPTool
* **examples** contains examples that demonstrate commonly used features of the UMLPTool
* **experiments** contains experiments that demonstrate edge cases of specific UMLPTool features
  and, at the same time, are integration tests for the UMLPTool
* **umlp-tool** contains the implementation of the language, tool, and generator
* **scripts** contains the scripts to setup the development environment

## Setup
Please run the `setup/npm_setup` script to setup npm registry. This is required for building and
running the client of the generated applications. The script creates an `.npmrc` file that tells
gradle (and npm) where to get the packages from and what credentials to use when downloading the
packages. This file is similar to `gradle.properties` of gradle or `settings.xml` of maven. There
are 3 versions of the same scripts: for PowerShell, bash, and Windows command terminal. Example
usage:
```
./npm_setup.sh username password
```
this creates the `.npmrc` in your home folder (where e.g., your global `gradle.properties` is).
`username` and `password` are your Nexus account username and password (you use them in global
`gradle.properties`). Alternatively:
```
./npm_setup.sh username password path/for/.npmrc
```
you can specify the path yourself and copy the file where you need it.
> Note: `.npmrc` files are ignored in the umlp project for security reasons

To check that the setup is done properly, run `gradle npm_config_list`, the result will contain something like that:
```
; userconfig home/folder/.npmrc
@monticore:registry = "https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/"
@umlp:registry = "https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/"
```
