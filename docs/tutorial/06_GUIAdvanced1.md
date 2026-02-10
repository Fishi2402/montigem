# Adding own functions

Previously we have built a very simple list of cars. Now we will build something
a bit closer to an actual page.

We start with a simple page again:

```java title="frontend/src/main/gui/gui/CarOverview.gui"
package gui;

import gui.navigation.NavContainer;
import mc.fenix.basic.GemText;
import mc.fenix.arrange.GemColumn;
import mc.fenix.basic.GemButton;
import Carrental.Car;
/* imports */

page CarOverview(List<Car> cars) {
  @NavContainer(content = [
    @GemColumn(components = [

      @GemText(value = getAvailableCarsTitle())

    ])
  ]);
}
```

Try adding the page to `NavContainer` yourself.

The `@GemText` in `CarOvervew` calls a function `getAvailableCarsTitle()` to get
the correct value. The function was never declared in the component, but the
generator assumes that you will implement it and creates an empty function.

!!! note 
    You could expect to be able to write something like `@GemText(value =
    getAvailableCarsAmount() + " available cars")`, but this is not allowed. The
    generator is unable to process more complex expressions containing method
    calls.

Let's add this function to `CarOverview.java`.
We can apply TOP-mechanism and override the empty function to show the number of
available cars:

```java title="frontend/src/main/java/gui/CarOverview.java"
package gui;

import carrental.Car;
import carrental.RentableStatus;
import umlp.jsweet.extension.annotation.Component;

@Component()
public class CarOverview extends CarOverviewTOP {

  public String getAvailableCarsTitle() {
    int i = 0;
    for (Car c : this.getCars()) {
      if (c.getRentableStatus() == RentableStatus.AVAILABLE) {
        i++;
      }
    }
    return i + " available cars";
  }
}
```

!!! note
    Just like backend Java files (e.g., `CarrentalServerApplication.java`),
    frontend Java classes are placed in the
    `frontend/src/main/java/<packageName>/` directory. In this case, the only
    package in this tutorial used is `gui`.

The `getAvailableCarsTitle` method checks if cars are loaded and counts amount
of available cars and return a string indicating the amount, e.g. `3 available
cars`.

!!! question
    "Where does the `this.getCars()` method come from?" The generator produces
    methods that are mapped from variables and attributes in class diagram and
    GUI component model. Getters and setters are generated for most class
    diagram attributes and GUI component parameters.

Some useful generated methods:

- Each GUI component parameter has a getter, for example, in `page
  CarOverview(List<Car> cars)` the `cars` is transformed into

    - `getCars` method
    - `initCars` method
    - `setCars` method
    - `_cars` attribute
    - `cars` pseudo-attribute (only in a TypeScript file)

- Each class attribute has a getter, for example, in `class Car { RentableStatus
  rentableStatus; }` the `rentableStatus` is transformed into

    - `getRentableStatus` method.
    - `setRentableStatus` method.
  
    !!! note 
        For `boolean` type attributes `is` prefix is generated. For example, 
        if attribute `boolean available` was defiend, `isAvailable` method would
        be generated instead of `getAvailable`.

- If an attribute is a list or is described as association with `*` cardinality
  additional methods can be used, for example, a `Customer` has an association
  to `RentalTransaction`: `association [*] RentalTransaction <-> Customer [1]`.
  For the `Customer` class following methods are generated:

    - `addRentalTransaction` method
    - `removeRentalTransaction` method
    - `sizeRentalTransactions` method
  

The method `getAvailableCarsTitle` (its TypeScript variant) is re-evaluated at
application run-time, when any related object, such as `cars`, changes. If the
evaluation result is different from the last method call, the corresponding GUI
part is automatically updated.

## Adding services

Let's extend the page to show cars and a button for renting them:
```java title="frontend/src/main/java/gui/CarOverview.java"
package gui;

import gui.navigation.NavContainer;
import mc.fenix.basic.GemText;
import mc.fenix.arrange.GemColumn;
import mc.fenix.basic.GemButton;
import Carrental.Car;

page CarOverview(List<Car> cars) {
  @NavContainer(content = [
    @GemColumn(components = [

      @GemText(value = getAvailableCarsTitle()),
      @iterate(Car c : cars) {
        @GemText(value = c.make + " " + c.model);
        @GemButton(label = "Rent car", leftClick = rentCar(c));
      }

    ])
  ]);
}
```
Here we iterate through cars and add a button for each car. The `rentCar` is a
custom function similar to `getAvailableCarsTitle`, but it expects an
`Consumer<Void>` (the TypeScript equivalent would be the `Event<void>` type) to
be returned. The generated code is:
```java
public Consumer<Void> rentCar(Car val) {
  return null;
}
```
The generator finds out what a function should return based on context. Here,
`leftClick` is an event that doesn't provide any value, thus the type is
`Consumer<Void>`.

!!! note
    The button could, for example, provide an id of a clicked button as a
    string, in which case the type would be `Consumer<String>`.

The `Consumer<Void>` type indicates a callback function that should be called
when an event triggers, e.g., when left click happens. This means that the
function `rentCar` has to return a function. For example, we want to call a
function that changes status of the car from available to rented when the button
is clicked. Thus, we implement the following method:
```java title="frontend/src/main/java/gui/CarOverview.java"
import java.util.function.Consumer;
/*other imports*/

public class CarOverview extends CarOverviewTOP {
  /*...*/

  @Override
  public Consumer<Void> rentCar(Car car) {
    return x -> {
      car.setRentableStatus(RentableStatus.RENTED);
    };
  }
}
```
the syntax `x -> { /*...*/ }` indicates a lambda
function. In TypeScript this would be an [arrow function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Arrow_functions).

We could stop there, but renting a car doesn't only mean changing its status, it
also means that the car is reserved for a customer, a customer transaction is
created, etc. The whole logic could be defined in the `rentCar` function, but a
better option is to use a service that would execute necessary actions.
[Previously](./05_ClassDiagramAdvanced.md) we defined such a service. We can use
it by executing corresponding commands via the `CommandManager`. For example: 
```java
import carrental.commands.RentItem;
import umlp.backendrte.command.CommandManager;
/*other imports*/

public class CarOverview extends CarOverviewTOP {
  @Override
  public Consumer<Void> rentCar(Car car) {
    return x -> {
      CommandManager.executeCommand(new RentCar(car.getGemId()));
    };
  }
  /*...*/
}
```

### Event Handling Best Practices

Currently, method overloading is not supported for event handler methods in the
GUI DSL (this might change in future versions). Instead of:
```java
@GemButton(leftClick=doSomething("A"));
@GemButton(leftClick=doSomething("B"));
```
Use unique method names in the GUI DSL and delegate to a common implementation
in Java:
```java
@GemButton(leftClick=doThingA());
@GemButton(leftClick=doThingB());
```
```java
public Consumer<Void> doThingA() { return v -> doSomething("A"); }
public Consumer<Void> doThingB() { return v -> doSomething("B"); }
private void doSomething(String param) { ... }
```
