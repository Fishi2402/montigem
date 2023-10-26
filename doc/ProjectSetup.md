# Setting up a new UMLP Project

Here it is explained how a project is set up and kicked off.

Each exemplary project has the following folder structure:

- Backend
- Frontend
- Models

## 1. Models

All models are located in the `models` directory. 
The domain model (for example, `Dtcockpit.umlp`) is placed in the main directory, and it is recommended to put all GUI models in the `gui` subdirectory.

Domain classes (classes defined in the domain model) can be used as parameters of GUI components in GUI models.
In this case domain classes have to be imported.  
For example, the import statement in the GUI model for the class `User` defined in the domain model `Dtcockpit.umlp` would look like this:
```java
import Dtcockpit.User;
```
## 2. HWC and configuration

The folders *backend/src/main/java* **(BE)** und *frontend/src/app* **(FE)** are where you can override - if necessary - all generated classes using the TOP mechanism.   
Most commonly, it's domain classes and commands that are overridden. It can also be deserializers or managers or something else, depending on the project's requirements and design.

Both BE and FE require specific configuration files. Refer to section 3 for BE and section 4 for FE for detailed explanations.
In order to deploy the project, the deployment configuration file needs to be extended accordingly.  
The following are sample entries in `.gitlab-ci.yml`:  
```
...
build:
stage: build
script:
- "gradle --version"
- "gradle build --refresh-dependencies --stacktrace -P mavenPassword=$pass -P mavenUser=$user $GRADLE_OPTS $ARGS1"
cache:
paths:
- ...
- examples/dtcockpit/frontend/build/frontendGen/node_modules
- examples/dtcockpit/frontend/.gradle/nodejs
artifacts:
paths:
- ...
- examples/dtcockpit/frontend/build/frontendGen/dist/**
- examples/dtcockpit/frontend/build/frontendGen/Dockerfile
- examples/dtcockpit/frontend/build/frontendGen/Caddyfile
- examples/dtcockpit/frontend/build/frontendGen/.dockerignore
```

## 3. BE

BE requires specific configuration files, which must be placed in the *backend/src/main/resources* directory.  
The configuration includes `database.properties` and several other optional files such as `application.properties`, `logback.xml`.

Examples for `database.properties`, depending on which database is used:
```java
jdbc_driver=org.h2.Driver
database_url=jdbc:h2:mem:umlpdb
user=...
password=...
```

```
jdbc_driver=org.neo4j.jdbc.bolt.BoltDriver
database_url=jdbc:neo4j:bolt://localhost:7687/?flatten=1
user=...
password=...
```

Here is an example for `application.properties`:
```java
server.servlet.context-path=/umlp/api
server.port=${PORT:8081}
umlp.service.websocket.impl=auth

# No caching in browser during dev, otherwise old versions are displayed
spring.web.resources.cache.cachecontrol.no-cache=true
spring.web.resources.cache.cachecontrol.no-store=true
spring.web.resources.cache.cachecontrol.must-revalidate=true
```

Besides the classes that can be overridden, there is a class where HWC implementation is required.  
It's a ServerApplication class where, in the *main()* method, the SpringApplication is started, and in the *init()* method, a new web server is created and dummy data is generated if necessary/desired.
 
Here is an example from the **DTCockpit** project:

```java
@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "dtcockpit.service", "security"}
)
public class DtcockpitServerApplication extends dtcockpit.DtcockpitServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(DtcockpitServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (DtcockpitManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      DtcockpitManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          ...
          .build();
    }
    ...
    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), DtcockpitServerApplication.class.getName());
      ...
    }
    catch (SQLException e) {
      throw new IOException(e);
    }
  }
    

```

## 4. FE

In the FE source directory, besides generated domain classes, can also generated components be overridden as well, for example if GUI actions must be defined.  
Here is an example for the `UserCreateComponent.ts`:
```
@Component({
selector: 'user-create',
templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
styles: [":host{ display: flex; flex-direction: column }"],
styleUrls: ["/Login.scss"]
})

export class UserCreateComponent extends UserCreateComponentTOP {

  public constructor(protected location: Location, protected router: Router) {
    super(router);
  }
  ...
  public override navigateBack(): void {
    this.location.back();
  }
}
```

FE also requires specific configuration files, which must be placed in the *frontend/config/src* directory.  
The configuration includes some files in the sub folder app, which describe the main component (root component) of a client application (will be generated later as well).

In AppComponent we usually initialize the generated implementations of Client- and CommandManager (can be overridden as well):
```
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    DtcockpitManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
    ws_protocol + '://' + document.location.host + '/umlp/api/command'
  ));
  CommandManager.initMe(cmdManager);
}
```

The application also needs AppModule (`AppGemModuleConfig.ts`), which imports other used modules and AppRouting (`AppGemRouting.ts`), which allows users to navigate between different parts of application.  

Icons, images and videos must be stored in the `config/src/assets` folder.

## 5. How to start the application

The project is built using Gradle.  

- To start the local server (using the DTCockpit project as an example), use the following command:
```
gradle :examples:dtcockpit:backend:runServer
```
- To start the client, use the following command:
```
gradle :examples:dtcockpit:frontend:runApp
```

Before starting the client, it is advisable to stop any running Node processes. Use the following command:
```
taskkill /f /im node.exe ;  gradle:examples:dtcockpit:frontend:runApp
```
