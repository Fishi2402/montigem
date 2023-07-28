# Native Image
GraalVMs native image can be used to compile a native executable from Java projects.
The resulting program has lower idle memory consumption and faster startup, but less peak throughput.
For long-running server applications this typically is not useful, execept when a lot of instances with low usage are started on a single server, e.g. in teaching scenarios where every student has one project.

You can build an OCI/Docker container containing the native executable with `gradle :examples:native-image:bootBuildImage` and start it with `docker run -it -p 8081:8081 registry.git.rwth-aachen.de/monticore/umlp/native_image_demo`.
Afterwards start the shell api with `gradle :examples:native-image:runShellApi`. Connect to the server with `init` and see all available commands with `help`.
