## Compare Files in Gradle Task

When developing generators, it is interesting to see how the generated code changed.
By copying a few lines, you can setup gradle this way.

In ``build.gradle`` of the subproject:
``````groovy
String diff_location="diff_store"
task copyGeneratedFiles(type: Sync) {
  group = "diff"
  from generateUmlpBackend.getOutputDir() // Maybe change task which produces output
  into layout.buildDirectory.dir(diff_location)
}

task diff(type: Exec){
  group = "diff"
  dependsOn(generateUmlpBackend)      // Maybe change task which produces output
  commandLine 'cmd', '/c', 'git', 'diff', '--no-index', layout.buildDirectory.dir(diff_location).get().getAsFile().toPath(), generateUmlpBackend.getOutputDir().get().getAsFile().toPath()
  ignoreExitValue true
}
``````

#### Usage:
* Setup Base-State which is used for comparison: ``gradle <subproject>:copyGeneratedFiles``
  * Only required at the beginning. If you always copy before comparing, there never will be any difference
* Change Generator, e.g. modify ``.ftl`` files
* Run ``gradle <subproject>:diff``
  * You can also add the ``--continuous`` flag to automatically compute the diff on each save
