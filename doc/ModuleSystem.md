
# MontiGem Module System

Modules provide re-usable $things that can be used in MontiGem applications.
They are able to accomplish this via the following four pillars:

* Modify the MontiGem generator via hook-points (cf. configTemplate)
* Provide (extensible) domain structures (including hand-written domain code)
* Provide hand-written code, such as an email service
* Provide GUI components and pages (including hand-written gui code)

For these purposes, modules "publish" (a subset of) the following artifacts, which are consumed by certain Gradle configurations:
* A generation-time artifact.jar (`umlpModulesGen` configuration / via the `--module-config` CLI option)
* A domain-symbols.jar (`umlpModulesDomainSymbols` configuration / via the `--path` symbolpath CLI option), containing the CD-symbols to be used with the Gui Generator
* A models.jar (`umlpModulesModels` configuration/ via the `--modelPath` CLI option), containing the original .cd models to be used with the Domain Generator
* A backend.jar (`implementation` configuration / added to the java classpath), containing the compiled backend sources 
* A ts-module, containing the typescript frontend sources

## Login Module Example

In the following, we will install the Login Module into our Car-Rental application.

We start by adding the login module to the backend:
```
// backend/build.gradle
dependencies {
  
  // TODO: NYI -  umlpModules("umlp.modules:login:${version}") // load the login module, consisting of models, config template tool adaption, and implementation/backend dependencies
  
  // Which resolves to the following individual dependencies in various configurations

  umlpModulesModels("umlp.modules:login:${version}") // load the login models
  umlpModulesGen("umlp.modules:login:${version}") // modify the MontiGem tool
  implementation("umlp.modules.login:backend:${version}") // add the login-backend to the classpath, as well as its dependencies
}
```
(Note: The three dependencies will be merged into a single dependency at some point)


We also have model, that the domain classdiagram extends the login module's classdiagram, 
 which we can accomplish by means of the `<<extends>>` stereotype and a corresponding import:

```cd
// model-components/CustomerService.umlp
import LoginModule.*;
<<extends="LoginModule">>
umlp CustomerService {
```

The [LoginModule.umlp](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/modules/login/models/LoginModule.umlp?ref_type=heads#L14)  classdiagram is now loaded from the `umlpModulesModels` configuration.

The import is important, try what happens if you forget it :)

### Class Diagram Extension Mechanism (Simple)
The Login Module defines a class `LoginModuleUser`,
 which is now available in our application's domain.

Similar to CD merge,
 but hwc is already provided (and loaded into the classpath via the `implementation` configuration).
For example, the `LoginSecManager.getCurrentUserRequest()` command is already implemented.

To actually use domain objects of an extended classdiagram, we have to use that diagrams manager:
For example, we add a default user on startup:

```
// CarRentalServerApplication.ftl
  public void init() throws IOException {
    // ...
    if (LoginModuleManager.getLoginModuleUserList().isEmpty()) {
        LoginModuleManager.loginModuleUserBuilder()
            .email("unknown")
            .username("ShadyCarz")
            .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
            .registrationDate(LocalDateTime.now())
            .build();
    }
```

(On this note: Similar to MontiCore Mills, the MontiGem Managers are also adapter to be used with Modules.
Thus, the modules managers do not have to be initialized explicitly.)

### Manual Backend changes
The *src/main/resources/application.properties* files has to be changed by hand, such as `noAuth` becomes `auth`:
```
// backend/src/main/resources/application.properties
umlp.service.websocket.impl=auth
```

### Backend Tool Modification
The login module causes the backend generation to run with the *login_module.backend.LoginBackendConfig.ftl* template:

```ftl
// login-module: LoginBackendConfig.ftl (read-only)
${mod.addModuleToHookpoint("umlp.generator.hook.BeforeTransform",  "login_module.backend.LoginBackend_BeforeTransform")}
```

This template binds the *LoginBackend_BeforeTransform* template to the *BeforeTransform* hookpoint to invoke an additional decorator.

```ftl
// login-module: LoginBackend_BeforeTransform.ftl (read-only)
<#assign decorator = tc.instantiate("login_module.SpringSecurityConfigDecorator", [glex]) />
${decorator.transform(ast, decoratedAST)}
```

This results in the generated *build/umlp/backend/carrental/service/CarRentalSecurityConfig.java* file,
 which sets up various password encoders and the login http endpoint.

### Adding the Login-Module to the Frontend:
Similar to the backend, we also have to add the login module to the frontend as a dependency:

```
// frontend/build.gradle
dependencies {
  umlpModulesGen("umlp.modules:login:$version") // Load generator modifications
  umlpModulesDomainSymbols("umlp.modules.login:frontendTS:$version") // frontend domain symbols
  umlpModulesModels("umlp.modules:login:$version") // and models itself
}
```

(Note: Due to car-rental being a weird project/a workaround for CD2GUI singletons, 
 those lines SHOULD NOT be added after the `genGUI` definition,
as otherwise gradle MIGHT complain about already resolved configurations.)

In addition, the typescript dependency (similar to the backends *implementation* dependency) has to be added:

```json
// frontend/config/package.json
 "dependencies": {
   ...
   "@umlp/login": "^7.6.1"
  }
```


 TODOS:
  * ToDo: Configure Module via public things (module-specific)
    * OD?
  * Extend the Module-User via extends
  
