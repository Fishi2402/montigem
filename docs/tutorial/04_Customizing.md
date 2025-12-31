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
custom start-up logic might require hand-written adaptations. The entry point
for adapting the applicatoin start-up is adapting the `ServerApplication` class,
e.g., `carrental.CarrentalServerApplication`. Here you can override the `init`
method with the Spring Boot `@PostConstruct` annotation. A typical use case for
this is the creation of start-up dummy data to test your product. You have
already worked with the `initEntities()` method to do so while learning [class
diagram basics](./01_ClassDiagramBasics.md#class).

```java title="backend/src/main/java/carrental/CarrentalServerApplication.java"
public class CarrentalServerApplication extends CarrentalServerApplicationTOP {

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();
    initEntities();
  }

  protected void initEntities() {
    // dummy data creation
  }
}
```

## Hand-Written Code Extensions: Create, Read, Update, Delete (CRUD) Operations

The main entry point to interact with any part of the stored domain objects is
the generated singleton class `Manager` (e.g., `carrental.CarrentalManager`,
`domain.DomainManager`). It offers methods to create, retrieve or delete
a domain object. For example, for a domain object `Car`:

* `Manager.getCar(gemId)` returns the object if present
* `Manager.getCarSure(gemId)` returns the object
* `Manager.getCarList()` returns all objects of the `Car` type
* `Manager.carBuilder()` returns a builder
* `Manager.deleteCar(...)` deletes the object if present

The Manager is available both in the backend and frontend. Both are synchronized
via a websocket connection. Note that synchronization is ensured between all 
clients and also the database.

!!! note 
    While the Java code looks synchronized, it locks in the background until
    synchronization finished.
    For the same reason and technical restrictions the TypeScript code looks
    slightly different and returns asynchronous
    promises instead of actual objects.
    
    Example Java:

    ```java
    Car c = CarrentalManager.getCarSure(4); 
    // work with c object
    ```

    Example TypeScript:

    ```ts
    CarrentalManager.getCarSure(4).then((c: Car) => {
      // work with c object
    });
    ```

Updating the objects happens by using setter methods of the objects. For
example:
```java
Car c = CarrentalManager.getCarSure(4); 
c.setModel("DeLorean"); // update car model
```
Synchronization with the manager and the server, all clients and the database
happens automatically in the background. Each domain object offers methods to
edit attributes and read associations.

## Synchronization

The communication between server and clients is implemented using a command
pattern and websockets. For each action, e.g., creating/updating a domain
object, MontiGem generates a command (Java/TS class) and corresponding
(de-)serializers converting objects into JSON. When executing an action, e.g.,
`car.setModel("DeLorean")`, these commands are exchanged between server and
clients via a websocket endpoint and executed. The logic for each command can be
overriden in its `CommandResult<T> doAction()` method. The server is responsible
to broadcast changes to all clients and the database. Commands can be send by
calling the singleton class `CommandManager`, e.g.,
`CommandManager.executeCommand(new Car_SetModelCommand(4, "DeLorean");`.
Sending the commands manually will be discussed in the next [tutorial
step](./05_ClassDiagramAdvanced.md).

### End-Points

Synchronization happens over the command websocket endpoint. Spring Boot
provides utilities to add end-points. You may need extra end-points to handle
binary data, for example, an endpoint to store a PDF file. To do so, add a new
class with the `@RestController` annotation. The location of the file does not
matter as far as it is in the backend source code. Spring Boot will find it and
take corresponding action:

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
