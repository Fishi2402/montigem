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

- A template can be found in :examples:sehub-cd2gui:frontend, for the tasks see below
- add a variable useCD2GUI which can be used to turn cd2gui toggle cd2gui
- copy the tasks `generateCD2GUI`, `guiVariability` and `genGUI2` into your project and change
  
  - `input` on `generateCD2GUI` to your domain file path
  - `input` on `guiVariability` to the handcoded model path
  - `domainModels` on `genGUI2` to the name of the domain Model
  - possibly the `input` on your main `genGUI` Task to `file(guiVariability.getOutputDir().dir("yourHandcodedPackageName"))` if the guiVariability functionality is needed for the main project

- Be sure to add the the genGUI2 to the npm prerequisites and change the `@src/*` to also include `../umlp/fe2/*`
- Add cd2gui to the routing pages `{ path:"cd2gui", loadChildren:() => import('../../build/umlp/fe2/cd2gui/models/ModelsGemRouting').then(m=>m.routes)}`
- Add a link to the CD2GUI Dashboard (`cd2gui/models/CD2GUIDashboard`) or navigate there manually
- Note: when turning cd2gui on and off make sure to disable any caching for the first run

## Stereotypes

CD2GUI uses the following Stereotypes

- invisible - used for a class, enum or attribute. An invisible Class doesn't get pages generated and an Enum/ Attribute won't show on other pages
- hidden_in_details - used for a class, enum or attribute. A Class doesn't get the details page generated and an Enum/ Attribute won't show on details pages
- hidden_in_overview - used for a class, enum or attribute. A Class doesn't get the overview page generated and an Enum/ Attribute won't show on overview pages

## Using GuiVariability to adapt a generated GUI Model

- CD2GUI supports GUIVariability by adding meaningful component names refer to
- All named components can be replaced, deleted and altered with GUIVariability, for the specific naming system please refer to the relevant templates
- Please note that altering existing Pages might impact the functionality of both the page itself and other pages.
- For more information refer to GUIVariability

### Example

The CD2GUI Page `AddressOverview` contains a subclass Section that is not needed

- create a `yourFileName.gui` file in the hwc directory
- look up the Name of the respective component - `AddressOverviewSubclassCard` in this case
- The new file should look as follows

```
package yourPackage;

page yourFileName() adapts AddressOverview{

    remove AddressOverviewSubclassCard;

}

```



## Gradle Tasks

```
tasks.register('generateCDTOGUI', UMLPCdToGuiTask) {
enabled(useCD2GUI)
setGroup('build')
input = file("yourDomainFile")
outputDir = layout.buildDirectory.dir("cd2gui")
}
```
```
tasks.register('guiVariability', GuiVariabilityHandlerTask) {
if (useCD2GUI) {
input.from(generateCDTOGUI.getOutputDir().dir("models"))
}
input.from(file("yourHWCDirectory"))
outputDir = layout.buildDirectory.dir("guiVariability")
}
```
```
tasks.register('genGUI2', UMLPGuiGenTask) {
  enabled(useCD2GUI)
  setGroup('build')
  dependsOn(genGUI, guiVariability)
  input = file(guiVariability.getOutputDir().dir("cd2gui"))
  symbolDir = files(
      generateCDTOGUI.getOutputDir().file("symbols"),
      project(":umlp-rte:client").tasks.createGuiSymbols.output
  )
  hwcDir = file("yourHWCDirectory")
  symbolOutDir = layout.buildDirectory.dir("umlp/fe2/symbols")
  outputDir = layout.buildDirectory.dir("umlp/fe2")
  domainModels = ["yourDomainName"]
  guiCreateModelsAsFile = file(generateCDTOGUI.getOutputDir().file("meta/guiCreateModels.txt"))
}

```

