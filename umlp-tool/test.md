# Table of Contents

- [Basic Introduction](#basic-introduction)
- [Routes Generator](#routes-generator)
  - [AppRouteGeneration](#approutegeneration)
  - [PackageRouteGeneration](#packageroutegeneration)
  - [TOP Mechanism](#top-mechanism)
- [Module Generator](#module-generator)
  - [AppModuleGeneration](#appmodulegeneration)
  - [PackageModuleGeneration](#packagemodulegeneration)
  - [Config Mechanism](#config-mechanism)

## Basic Introduction

The **Config Generator** consists of two main components: the *Routes Generator* and the *Module Generator*. The *Routes Generator* produces the `**GemRouting.ts`, while the *Module Generator* creates the `**GemModule.ts`.

### Routes Generator

#### AppRouteGeneration

- *File Location*: `build/frontendGen/build/app/AppGemRouting.ts`
- *Purpose*: Generates routing for the entire project. It should include routing information to different packages directly under the `models` directory. For instance, if there are two packages, `gui` and `person`, under the `models` directory, the routing should be defined for the `gui` and `person` modules separately, with the package name as the path.

#### PackageRouteGeneration

- *File Location*: `(package name with first letter capitallized)GemRouting.ts` is generated in the `build/frontendGen/build/app/(path to the package)` directory.
- *Purpose*: Generates routing files for all pages within a package. For example, if there are pages like `teacher` and `student`, routing should be established for each page, with the path being the relative path from the `models` directory to the page.

#### TOP Mechanism

- *Support*: Hand-written routing files
- *Usage*: Fof instance, if there is a hand-written `GuiGemRouting.ts` in `src/app/gui`, then `GuiGemRoutingTOP.ts` will be in the `(build directory path)/gui/`.

### Module Generator

#### AppModuleGeneration

- *File Location*: `build/frontendGen/build/app/AppGemModule.ts`
- *Purpose*: Generates the module file for the entire project. It should include basic components and modules.

#### PackageModuleGeneration

- *File Location*: `(package name)GemModule.ts` is generated in the `build/frontendGen/build/app/(path to the package)` directory.
- *Purpose*: Generates module files for pages within a package. It should include all the modules and components needed for every page within the package. Ensure that all page components are declared in the generated module file. Also, import the routing for the package. Additionally, import the module of the sub-package in the package.

#### Config Mechanism

- *Usage*: Used to support hand-written module config, as Angular module files don't support export. If there is a hand-written `GuiGemModuleConfig.ts` in `src/app/gui`, then `GuiGemModule.ts` will be in the `(build directory path)/gui/` and will import the configuration from `GuiGemModuleConfig` to it.
