# Adding services to the class diagram

Besides managing data an app may do other tasks, such as sending e-mails or
providing user authentication. Those tasks are typically managed through one or
several services that you can introduce via the class diagram. A (static)
service is annotated with the `<<singleton>>` stereotype and corresponding
fields/methods. For example, add to the class diagram:
```java
umlp Carrental {

  <<singleton>>
  class RentalService {
    RentalTransaction rentItem(RentableItem rentable);
    void returnItem(RentalTransaction rentalTransaction);
    void processPayment(RentalTransaction rentalTransaction);
  }

}
```

For each method specified in the singleton class, MontiGem generates a command
which requires custom implementation via TOP-mechanism. For example,
`backend/src/main/java/carrental/commands/RentItem.java`:
```java
package carrental.commands;

import carrental.RentalTransaction;
import umlp.backendrte.command.SimpleResult;

public class RentItem extends RentItemTOP {
    
  public RentItem(long rentableGemId) {
    super(rentableGemId);
  }

  @Override
  public SimpleResult<RentalTransaction> doAction() {
    RentalTransaction result;
    // your code
    return new SimpleResult<>(getId(), result, RentalTransaction.class.getName());
  }
}
```
Now add a dummy implementation for all three commands defined in
the singleton above.

The singleton can then be used in the frontend and backend code. The example `RentItem`
command is sent using a `CommandManager`:
```java
CommandManager.executeCommand(new RentItem(car.getGemId()));
```

The result of a command is a
`CommandResult` that contains whatever result is set as a return type of the
command method.
```java
CommandResult<RentalTransaction> res = CommandManager.executeCommand(new RentItem(car.getGemId()));
```

## Global State Management

Singletons are also useful for managing global state in the frontend. You can define a singleton class to hold the state of your application.

```java
umlp Carrental {

  <<singleton>>
  class App {
    String selectedTopic;
    // other state variables
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
