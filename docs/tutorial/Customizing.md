# Customzing the App

As mentioned for the TOP-mechanism, hand-written code adaptions are necessary
for a full stack application. Here is where and how to adapt the different parts
of the MontiGem-generated code.

## Customizing the Server Application

MontiGem generates server code to run the backend. The used technology framework
is [Spring Boot](https://spring.io/projects/spring-boot) which provides the
tools to enable server applications, end points, security, and more.
The generated code is adaptable by using the TOP-mechanism.

### Start-up

The server application is generated from the UML class diagram. However, some
custom start-up logic might require hand-written adaptions.
The entry point for adapting the applicatoin start-up is adapting the
`ServerApplication` class, e.g.,
`carrental.CarrentalServerApplication`. Here you can override the `init` method
with the Spring Boot `@PostConstruct` annotation. A typical use case for this
is the creation of start-up dummy data to test your product.
We already provided an empty method `initEntities()` to do so.
Try it yourself, by implementing this method and instantiating classes,
you modeled in the class diagram.

```java
public class CarrentalServerApplication extends CarrentalServerApplicationTOP {

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();
    // your code, e.g., dummy data creation
  }

  /*...*/
}
```

## Interaction with the Code

The main entry point to interact with any part of the stored domain objects is
the generated singleton class `Manager` (e.g., `carrental.CarrentalManager`,
`domain.DomainManager`). It offers methods to create, retrieve or delete a
domain object. E.g., for a domain object `Foo`:
* `Manager.getFoo(gemId)` returns the object if present
* `Manager.getFooSure(gemId)` returns the object 
* `Manager.fooBuilder()` returns a builder
* `Manager.deleteFoo(...)` deletes the object if present

The Manager is available both in the backend and frontend. Both are synchronized
via a websocket connection. Note that synchronization is ensured between all 
clients and also the database.
 > Note: While the java code looks synchronized, it locks in the backround until
 > synchronization finished.  
 > For the same reason and technical restrictions the TypeScript code looks
 > slightly different and returns asyncrhonous
 > promises instead of actual objects. For
 > example, Java: `Customer c = CarrentalManager.getCustomerSure(4);`  
 > TS: `CarrentalManager.getCustomerSure(4).then((c: Customer) => {
 > console.log(c); });`

Altering the objects happens by editing the objects directly. Synchronization
with the manager and therefore also the server, all clients and the database 
happens automatically in the background. Each domain object offers the methods
to edit attributes (also all list operations) and navigate through associations.

## Synchronization

The communication between back- and frontend, server and clients, is
implemented using a command pattern and websockets. For each action, e.g.,
adding/altering a domain object, MontiGem generates a command (Java/TS class)
and corresponding De-Serializers (JSON). When executing an action (e.g., 
`car.setModel("DeLorean DMC-12")`), these
commands are exchanged between server and clients via a websocket endpoint.
Then on the other end, the command is executed. The logic for each command
can be overriden within its `CommandResult<T> doAction()` method.
The server is then responsible to broadcast changes to all clients and the
database.  
Commands can be send manually by calling the singleton class `CommandManager`,
e.g., `CommandManager.executeCommand(new Car_SetModelCommand(4, "DeLorean DMC-12");`

### End-Points

Synchronization happens over the command websocket endpoint.
If there is need to add new end-points, Spring Boot provides lots of utility
for this. E.g., an endpoint to store a PDF file a new class can
be added with the `@RestController` annotation. The precise location of the file does
not matter (unless it is still in the backend source code). 
Spring Boot will find it and take corresponding action:

```java
package carrental.somewhere;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
@RestController
public class PDFEndpoint {
  @PostMapping(value="/pdf")
  public ResponseEntity<String> storePDF(
    @RequestPart("file") MultipartFile file,
    @RequestHeader("name") String name
  ) {
    // your code
  }
}
```

[Next page](./ClassDiagramAdvanced.md)

[Previous page](./TOPMechanism.md)
