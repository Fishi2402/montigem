# Table of Contents

- [Basic Introduction](#basic-introdcution)
- [Routes Generator](#router-generrator)
  - [AppRouteGeneration](#app-config-generation)
  - [PackageRouteGeneration](#package-config-generation)
  - [TOP Mechanism](#top-mechanism)
- [Module Generator](#module-generrator)
  - [AppModuleGeneration](#app-module-generation)
  - [PackageModuleGeneration](#package-module-generation)
  - [Config Mechanism](#config-mechanism)




## Basic Introduction

Config Generator consists of two part. One is Routes Generator, which generates **GemRouting.ts and another is 
Module Generator which generates **GemModule.ts.

Routes Generator has two parts. one is for generating routes file for the complete project, 
which should be app.routing.ts without the generator. Another one is for generating routes file for the page within a package. 

Module Generator has two parts. one is for generating module file for the complete project,
which should be app.module.ts without the generator. Another one is for generating module file for the page within a package.

## Routes Generator

### AppRouteGeneration

AppGemRouting.ts is generated in the build/frontendGen/build/app/.
it should include the routing to the different package directly under the models directory. For example, if there are two packages, gui and person, under the model directory,
there should be routing to gui module and person module separately. The path to the corresponding module is the package name.

### PackageRouteGeneration

Unlike AppRouteGeneration, (package name)GemRouting.ts is generated in the build/frontendGen/build/app/(path to the package).
it should include all of the routing to the different page. For example, if there are too pages, teacher and student. 
there should be routing to teacher page and person page separately. The path to the corresponding page is the relative path from the models directory to the page.

### TOP Mechanism
Hand-written routing file is supported by TOP Mechanism. if there is a hand-writen
GuiGemRouting.ts in src/app/gui, then GuiGemRoutingTOP.ts would in (build directory path)/
gui/


## Module Generator

### AppModuleGeneration

AppGemModule.ts is generated in the build/frontendGen/build/app/.
it should include basic components and modules.

### PackageModuleGeneration

Unlike AppModuleGeneration, (package name)GemModule.ts is generated in the build/frontendGen/build/app/(path to the package).
it should include all of the modules and component every page in the package needs and also every page components should be declared in the generated module file. Also the module of the sub-package in the package is also imported.

### Config Mechanism

Because module file doesn't support export in angular, config Mechanism is used to support hand-written module config.
if there is a hand-writen
GuiGemModuleConfig.ts in src/app/gui, then GuiGemModule.ts would in (build directory path)/
gui/ and it would export the config in GuiGemModuleConfig to it. 
