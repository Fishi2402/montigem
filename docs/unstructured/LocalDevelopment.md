# Local development

When making changes(to the `umlp-tool`, Java RTE, or Typescript RTE) all subprojects within this (`umlp`) project automatically apply the changes.
This is not the case for MontiGem projects outside this repo.

## Typescript RTE
To use the changes to the Typescript RTE in another project you need to update your project/frontend/config/tsconfig.json by adding the changed part of the RTE to paths.
Here `networkgraph` was changed and `networktest` is an arbitrary but unique folder:

````json
"paths": {
"@umlp/networkgraph": ["networktest"],
...
}
````

You also need to make the actual code available to the project via the `symlinkCreation` gradle task in project/frontend/build.gradle.
Note that you need to use the same directory name as in the tsconfig.json changes.
Here it is `networktest`.

````gradle
symlinkCreation {
  def networkTest = file("$buildDir/frontendGen/networktest")
  def networkTestTarget = file("$PATH_TO_UMLP/umlp/umlp-rte/frontend/projects/networkgraph")
  getSymlinkMap().put(networkTest, networkTestTarget)
}
````

## Java RTE
Use the property `useLocalRepo=true` in project/gradle.properties and `gradle publishToMavenLocal` in the `umlp` project.
Alternative: add `includeBuild(".../umlp")` in project/settings.gradle.

## UMLP-Tool
Use the property `useLocalRepo=true` in project/gradle.properties and `gradle publishToMavenLocal` in the `umlp/umpl-tool` project.
Alternative: add `pluginManagement { includeBuild(".../umlp/umlp-tool") }` in project/settings.gradle.
