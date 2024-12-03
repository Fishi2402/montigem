<!--
If you make any changes to a library, please run `gradle build` to update the library version before publishing it.

# Creating a New Library
​
To create a new library, use the gradle script and run `gradle create library name`.
This script will assign the library a default version.
​
# Update a Library
​
Whenever changes are made to a library, use the gradle script to
update its version. For example, if a table has version 7.5.4
and you make changes to the table library, running `gradle build`
will upgrade the version from 7.5.4 to 7.5.5, as defined in the
gradle script.
-->
# Add library

Decide if you need a new library. When you may need a library:
- The components you build are specific to a domain. For example, your component
  displays circuit diagrams. Such component is unlikely useful in financial
  apps, stores, assistive sysmtes, or other apps unrelated to electrical
  engineering.
- The components you build depend on other libraries that are not included in
  any other existing library. For example, you use `sigma.js` to build a network
  graph. Components that depend on other libraries include the entire library
  they depend on in the compiled bundle, which may make the library
  significantly bigger.

Example setup:
- Example library (does not actually exist):
  [experimental](projects/umlp/experimental). `experimental` (full name:
  `@umlp/experimental`) can be replaced with any other library name in this
  instruction. For example, if you build library `foo`, every occurence of
  `experimental` must be replaced with `foo`.
- [Full library list](./projects/umlp)
- [Project root](./). Will be referred to as "root" in this instruction.

To create a library:
1.  Create a new folder `umlp/experimental`.
2.  Add file `umlp/experimental/README.md` and describe what you library
    contains and why it exists.
3.  Add file `umlp/experimental/ng-package.json` with following contents:
    ```json
    {
      "$schema": "../../../node_modules/ng-packagr/ng-package.schema.json",
      "dest": "../../../build/dist/umlp/experimental",
      "lib": {
        "entryFile": "src/index.ts"
      }
    }
    ```
4.  Add file `umlp/experimental/package.json` with following contents:
    ```json
    {
      "name": "@umlp/experimental",
      "version": "7.6.0",
      "peerDependencies": {
        "@angular/common": "^17.2.0",
        "@angular/core": "^17.2.0"
      },
      "dependencies": {
        "tslib": "^2.3.0"
      },
      "sideEffects": false
    }
    ```
    The `version` should be set to the current MontiCore release version. This
    rule is not strict but saves time looking for most recent and compatible
    library versions.

    **My library depends on an existing MontiGem library**. For example, the
    `@umlp/experimental` library uses a component from `@umlp/arrange`. In that
    case add the `@umlp/arrange` to peer dependencies:
    ```diff
    "peerDependencies": {
      "@angular/common": "^17.2.0",
      "@angular/core": "^17.2.0",
    + "@umlp/arrange": "^7.6.0"
    },
    ```
5.  Create `umlp/experimental/src/lib` folder and add your components in this
    folder. For example, you added `umlp/experimental/src/lib/MyComp.ts`
6.  Add file `umlp/experimental/src/index.ts`. This file exports the components,
    functions, services and other code that your library contains. The example
    content:
    ```ts
    export * from './lib/MyComp';
    ```
7.  Add following entry to `"projects"` object in `angular.json` file in root
    folder:
    ```json
    "@umlp/experimental": {
      "projectType": "library",
      "root": "projects/umlp/experimental",
      "sourceRoot": "projects/umlp/experimental/src",
      "prefix": "gem",
      "architect": {
        "build": {
          "builder": "@angular-devkit/build-angular:ng-packagr",
          "options": {
            "project": "projects/umlp/experimental/ng-package.json"
          },
          "configurations": {
            "production": {
              "tsConfig": "tsconfig.prod.json"
            },
            "development": {
              "tsConfig": "tsconfig.json"
            }
          },
          "defaultConfiguration": "production"
        },
        "test": {
          "builder": "@angular-devkit/build-angular:karma",
          "options": {
            "tsConfig": "tsconfig.spec.json",
            "polyfills": [
              "zone.js",
              "zone.js/testing"
            ]
          }
        }
      }
    }
    ```
8.  Add the library to the list of libraries in `build.gradle` file in the root:
    ```diff
    def libs = [
      ...,
    + "experimental"
    ]
    ```

    **My library depends on an existing MontiGem library**. For example, the
    `@umlp/experimental` library uses a component from `@umlp/arrange`. In that
    case add the dependency:
    ```groovy
    tasks.build_experimental.inputs.files(tasks.build_arrange.outputs.files.singleFile)
    tasks.build_experimental.dependsOn(tasks.build_arrange)
    ```
    If you introduce a circular dependency, you need to reconsider your library
    structure.


# Updating a library

Example setup
- Example library: [arrange](projects/umlp/arrange). `arrange` (full name: `@umlp/arrange`) can be
  replaced with any other library name in this instruction.
- [Full library list](./projects/umlp)
- [Project root](./). Will be referred to as "root" in this instruction.

Whenever changes are made to a library, the version needs to be updated in the `package.json` file
of the library. Example (all references to the `arrange` example should be replaced with your own
library):

- A file is changed in `arrange` library, i.e. any file inside [the library
  folder](projects/umlp/arrange).
- Open [`package.json` file](projects/umlp/arrange/package.json) of the changed library. 
- Increment the version number of the `"version"` field.
  - last version number if you make a patch (bug fix, minor stuff) or a backward compatible change,
    e.g., current value is `"7.5.1"`. Set new value `"7.5.2"`.
  - middle version number if you add a backward compatible feature, e.g., current value is
    `"7.5.12"`. Set new value `"7.6.0"`.
  - first version number if you make a breaking change, e.g., current value is `"7.5.12"`. Set new
    value according to MontiCore version `"8.0.0"`.
- Commit and push changes

# Using a library

Example setup: see [Updating a library](#updating-a-library).

- add dependency to the library in your project's `package.json` file:
  ```
  "dependencies": {
    ...,
    "@umlp/arrange": "^7.5.0"
  }
  ```
  - In case of a Fenix-local example (`examples` folder) add the dependency to the `package.json`
    [default configuration](../../umlp-tool/src/main/resources/frontend/config/package.json) if not
    done already. Alternatively overwrite the `package.json` file by providing your own
    configuration.
- add dependency to the GUI symbols library in your project's frontend and backend `build.gradle`
  files if not done already (similar change for both):
  ```gradle
  // add a dependency to new symbols for the genGUI task
  task genGUI(type: UMLPGuiGenTask) {
    dependsOn(genDomain, tasks.getByPath(":umlp-rte:client:createGuiSymbols"))
    /*...*/
    symbolDir = files(
      /*...*/
      project(":umlp-rte:client").tasks.createGuiSymbols.output
    )
  }
  ```

<!--
# Usage of the Library
​
To use the library, add the following line to your `package.json` file:
"library name": "^7.5.0". The "^" symbol ensures that the latest version of the library is used.
-->
