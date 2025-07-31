## Adding services to the class diagram

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
```ts
CommandManager.executeCommand(new RentItem(car.getGemId()));
```

The result of a command is a
`CommandResult` that contains whatever result is set as a return type of the
command method.
```ts
CommandResult<RentalTransaction> res = CommandManager.executeCommand(new RentItem(car.getGemId()));
```

[Next page](./GUIAdvanced1.md)

[Previous page](./Customizing.md)
