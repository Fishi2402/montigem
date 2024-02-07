<!-- (c) https://github.com/MontiCore/monticore -->

This document describes rules for configuring the build process.

Terminology:
- **Task.** A unit of work. [Read
  more](https://docs.gradle.org/current/userguide/tutorial_using_tasks.html)
- **Input and output.** A task implementation has annotated properties that
  define inputs and outputs of a task. The inputs and outputs define whether the
  task is complete and if it is up-to-date. For example, if you run a task for
  the first time it is neither complete nor up-to-date. If task `Foo` creates a
  file `bar` in a specific folder, the folder is an **input** and the `bar` is
  an **output**. [Read
  more](https://docs.gradle.org/current/userguide/incremental_build.html)

Rules:
- Multiple tasks must not have the same output folder. Current exception:
  `nodeSetup` and `frontedSetup` work with the same output folder.
- `npmInstall` tasks must not run in parallel. This is configured in
  [`examples/build.gradle` file](../examples/build.gradle).
  The lines:
  ```groovy
  project(":examples:dtcockpit:frontend").tasks.npmInstall.mustRunAfter(
    project(":examples:dtcockpit-jsweet:frontend").tasks.npmInstall
  )
  ```
  mean that if `npmInstall` task of two projects must be executed, it will be
  done sequentially (the order is defined but not relevant in this case). If
  `npmInstall` is executed only for one project, the other `npmInstall` tasks
  will not be triggered.

  Similar configuration must be done for every new subproject with an
  `npmInstall` task.

  Reason: `npmInstall` installs Chromium on a machine to be able to run frontend
  tests. If several tasks try to install Chromium, they may fail as they try to
  concurrently access the same (locked by one of the tasks) resource.
