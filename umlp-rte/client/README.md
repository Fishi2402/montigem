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
# Example setup

- Example library: [arrange](./projects/umlp/arrange). `arrange` (full name: `@umlp/arrange`) can be
  replaced with any other library name in this instruction.
- [Full library list](./projects/umlp)
- [Project root](./). Will be referred to as "root" in this instruction.

# Updating a library

Whenever changes are made to a library, the version needs to be updated in the `package.json` file
of the library. Example (all references to the `arrange` example should be replaced with your own
library):

- A file is changed in `arrange` library, i.e. any file inside [the library
  folder](./projects/umlp/arrange).
- Open [`package.json` file](./projects/umlp/arrange/package.json) of the changed library. 
- Increment the version number of the `"version"` field.
  - last version number if you make a patch (bug fix, minor stuff) or a backward compatible change,
    e.g., current value is `"7.5.1"`. Set new value `"7.5.2"`.
  - middle version number if you add a backward compatible feature, e.g., current value is
    `"7.5.12"`. Set new value `"7.6.0"`.
  - first version number if you make a breaking change, e.g., current value is `"7.5.12"`. Set new
    value according to MontiCore version `"8.0.0"`.
- Commit and push changes

# Using a library

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
