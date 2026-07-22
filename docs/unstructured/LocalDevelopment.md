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

Use the lazy `tasks.named(...)` form — an eager `createSymlink { ... }` block forces task
creation during configuration and can fail with cross-project ordering errors in multi-app
builds (e.g. `Task with name 'gui2java2ts' not found in project ':base:frontend'`):

````gradle
tasks.named("createSymlink") {
  def networkTest = file("$buildDir/frontendGen/networktest")
  def networkTestTarget = file("$PATH_TO_UMLP/umlp/umlp-rte/frontend/networkgraph")
  getLinks().put(networkTest, networkTestTarget)
}
````

Note on the link location: the tsconfig `paths` entries resolve relative to the app's
`baseUrl`. In projects where the Angular app is assembled in `frontend/build/app/` (baseUrl
`./`), place the link there instead, e.g. `file("$buildDir/app/networktest")`.

**Warning (Windows):** `createSymlink` creates a directory *junction*. Junctions forward
recursive deletes into the target — a `gradle clean` (or any task that wipes `build/`)
in the consuming project can then **delete files inside your umlp checkout**. Prefer a
`Sync` copy task instead of the link when working on Windows:

````gradle
def copyLocalCommonj2ts = tasks.register("copyLocalCommonj2ts", Sync) {
  from("$PATH_TO_UMLP/umlp/umlp-rte/frontend/commonj2ts") {
    exclude("build/**", "node_modules/**")
  }
  into(layout.buildDirectory.dir("app/commonj2ts-local"))
}
tasks.named("createSymlink") {
  dependsOn(copyLocalCommonj2ts)
}
````

## Java RTE
Use the property `useLocalRepo=true` in project/gradle.properties and `gradle publishToMavenLocal` in the `umlp` project.
Alternative: add `includeBuild(".../umlp")` in project/settings.gradle.

## UMLP-Tool
Use the property `useLocalRepo=true`    in project/gradle.properties and `gradle publishToMavenLocal` in the `umlp/umpl-tool` project.
Alternative: add `pluginManagement { includeBuild(".../umlp/umlp-tool") }` in project/settings.gradle.
