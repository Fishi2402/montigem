# CD2GUI
Plugin providing Guidsl2 models to create and manage instances of a provided CD4A Model


## Supported CD2GUI Features

- Displaying instances of any valid Class Diagram both in overview and detailed view
- Changing primitive Attributes, as well as Optionals of them
- showing associations between the classes
- disabling individual pages or page categories

## Currently not supported CD2GUI Features

- adding or removing associations
- creating new instances of a class
- deleting instances of a class 
- editing complex Attributes


## Adding CD2GUI to your Project

- A template can be found in :examples:sehub-cd2gui:frontend
- add a variable useCD2GUI which can be used to turn cd2gui toggle cd2gui
- copy the tasks `generateCD2GUI`, `guiVariability` and `genGUI2` into your project and change
  
  - `input` on `generateCD2GUI` to your domain file path
  - `input` on `guiVariability` to the handcoded model path
  - `domainModels` on `genGUI2` to the name of the domain Model
  - possibly the `input` on your main `genGUI` Task to `file(guiVariability.getOutputDir().dir("yourHandcodedPackageName"))` if the guiVariability functionality is needed for the main project

- Be sure to add the the genGUI2 to the npm prerequisites and change the `@src/*` to also include `../umlp/fe2/*`
- Note: when turning cd2gui on and off make sure to disable any caching for the first run

## How to adapt a generated GUI Model

- CD2GUI supports GUIVariability by adding meaningful component names refer to
- All named components can be replaced, deleted and altered with GUIVariability, for the specific naming system please refer to the relevant templates
- Please note that altering existing Pages might impact the functionality of both the page itself and other pages.
- For more information refer to GUIVariability



