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
  `npmInstall` task. Otherwise, an error will appear:
  ```
  npm ERR! code 1
  npm ERR! path /builds/monticore/umlp/examples/<my-project>/frontend/build/frontendGen/node_modules/puppeteer
  npm ERR! command failed
  npm ERR! command sh -c -- node install.mjs
  npm ERR! chrome-headless-shell (121.0.6167.85) downloaded to /root/.cache/puppeteer/chrome-headless-shell/linux-121.0.6167.85
  npm ERR! Error: ERROR: Failed to set up Chrome v121.0.6167.85! Set "PUPPETEER_SKIP_DOWNLOAD" env variable to skip download.
  npm ERR!     at file:///builds/monticore/umlp/examples/<my-project>/frontend/build/frontendGen/node_modules/puppeteer/lib/esm/puppeteer/node/install.js:58:23
  > Task :examples:selib:compileTestJava NO-SOURCE
  > Task :examples:<my-project>:frontend:npmInstall
  npm ERR!     at async Promise.all (index 0)
  npm ERR!     at async downloadBrowser (file:///builds/monticore/umlp/examples/<my-project>/frontend/build/frontendGen/node_modules/puppeteer/lib/esm/puppeteer/node/install.js:87:9) {
  npm ERR!   [cause]: Error: unexpected end of file
  npm ERR!       at Zlib.zlibOnError [as onerror] (node:zlib:189:17) {
  npm ERR!     errno: -5,
  npm ERR!     code: 'Z_BUF_ERROR'
  npm ERR!   }
  npm ERR! }
  ```

  Reason: `npmInstall` installs Chromium on a machine to be able to run frontend
  tests. If several tasks try to install Chromium, they may fail as they try to
  concurrently access the same (locked by one of the tasks) resource.

- Frontend uses `Karma` for testing. Although `Karma` is deprecated and has
  dependency on outdated packages, which output warnings:
  ```
  npm WARN deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
  npm WARN deprecated read-package-json@7.0.1: This package is no longer supported. Please use @npmcli/package-json instead.
  npm WARN deprecated rimraf@3.0.2: Rimraf versions prior to v4 are no longer supported
  npm WARN deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported
  ```
  the only alternatives are `Web Test Runner` and `Jest`. Both libraries have
  only experimental support not meant for production and are missing necessary
  features (most importantly source map) for proper usage with Angular
  framework. Between (1) doing nothing and staying with a deprecated library or
  (2) making changes and migrating to experimental libraries, option (1) was
  chosen until any significant update.

  If the warnings need to be suppressed, add following to `build.gradle` in
  `frontend` folder:
  ```
  npmInstall.args = ['--loglevel', 'error']
  ```