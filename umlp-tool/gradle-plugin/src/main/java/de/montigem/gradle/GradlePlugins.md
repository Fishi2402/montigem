# Gradle Plugin Documentation

## Common
### UMLPPluginBase
Base Plugin:
 registers tasks-types,
 loads UMLPPluginDep,
 registers statistics,
 sets up [montigemModule](../../../../../../doc/ModuleSystem.md) configurations.

### UMLPPluginDep
Uses the _buildInfo.properties_ file to load the umlp-tool (.jar) 
 and its dependencies into the `umlpTool` gradle configuration.
This configuration is then added to the various tasks types. 

## Backend

## Frontend


## Defining Modules
### UMLPPluginModule
Plugin added to the "root" project with the "models" directory.
Publishes the models.jar, containing the models,
 and the moduleGenTime.jar, containing generator modifications.

### UMLPPluginModuleBackend
Plugin added to the backend project.
(does nothing right now)

### UMLPPluginModuleFrontend
Plugin added to the js frontend project.
Publishes the domain Symbols and a bundle of the ts sources.
