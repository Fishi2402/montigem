# KIZAM backend

## Requirements
- Gradle 7.4
- Java 11
- Also setup these versions in IntelliJ
  - https://www.jetbrains.com/help/idea/gradle-settings.html
  - https://www.jetbrains.com/help/idea/sdk.html#define-sdk

## Execution
The server can be started with `gradle :products:kizam:backend:bootRun`

To debug the server in IntelliJ, navigate to `KizamServerApplication` and click run > debug next to the class name.
Debugging the gradle task does not work.

# Class diagram
The generated data structure can be adjusted by changing `models/Kizam.umlp`.
To add data during startup, `KizamServerApplication#init` can be modified.

# Endpoints
The example endpoint from `ExampleEndpoint` is reachable after starting the backend via
`curl.exe "http://localhost:8081/umlp/api/helloWorld"`
