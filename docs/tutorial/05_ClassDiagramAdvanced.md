# Adding services to the class diagram

Besides managing data an app may do other tasks, such as sending e-mails or
providing user authentication. Those tasks are typically managed through one or
several services that you can introduce via the class diagram. A (static)
service is annotated with the `<<singleton>>` stereotype.
For example, add to the class diagram:
```java title="models/Carrental.umlp"
umlp Carrental {
  /* other classes */

  <<singleton>>
  class RentalService {
    void rentCar(Car rentedCar);
    // void returnCar(Car rentedCar);
    // void processPayment(RentalTransaction rentalTransaction);
  }
}
```

For each method specified in the singleton class, MontiGem generates a command
which requires custom implementation via TOP-mechanism. For now, we will work
with a single `rentCar` method, you can try commenting in and implementing
other methods later.

Let's add an attribute of type `RentableStatus` to the `Car` class. You may use
it in the next step:
```java title="models/Carrental.umlp"
umlp Carrental {
  /* other classes */

  class Car {
    /* other attributes */
    RentableStatus rentableStatus = RentableStatus.AVAILABLE;
  }
}
```
The `rentCar` method calls the `RentCar` command. Try to implement the
`doAction` method yourself (hint: `rentedCarGemId` is the variable of `RentCar`
class holding an ID of the `rentedCar`):
```java title="backend/src/main/java/carrental/commands/RentCar.java"
package carrental.commands;

import carrental.Car;
import carrental.CarrentalManager;
import carrental.RentableStatus;
import umlp.backendrte.command.StatusResult;

public class RentCar extends RentCarTOP {

  public RentCar(long rentedCarGemId) {
    super(rentedCarGemId);
  }

  @Override
  public StatusResult doAction() {
    // TODO: change car status
    return StatusResult.ok(getId());
  }
}
```

!!! note
    You may run `gradlew backend:umlp2java` command in the project folder
    to generate backend code anew. If you work in an IDE, hand-written
    extensions will have missing symbols without the generated code.

The singleton can then be used in the frontend and backend code. The example
`RentCar` command is sent using a `CommandManager`:
```java
CommandManager.executeCommand(new RentCar(car.getGemId()));
```
We will come back to this command later.

<!--
## Global State Management

Singletons are also useful for managing global state in the frontend. You can
define a singleton class to hold the state of your application.

```java
umlp Carrental {

  <<singleton>>
  class App {
    String selectedTopic;
  }

}
```

This `App` singleton can be passed to pages and components to share state.

1.  **Pass `App` to the page:**
    ```java
    page MyPage(App app) { ... }
    ```
2.  **Pass it to child components:**
    ```java
    @MyChildComponent(app = app);
    ```
3.  **Modify state:**
    ```java
    getApp().setSelectedTopic("New Topic");
    ```
4.  **React to changes:**
    Components that use `app.selectedTopic` (e.g., in `@guard` or as a parameter) will automatically update when the value changes.

This approach provides a "Single Source of Truth" and avoids manually passing callbacks through many layers of components.

-->