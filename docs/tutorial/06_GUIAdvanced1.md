# Adding own functions

Previously we have built a very simple list of cars. Now we will build something
a bit closer to an actual page.

We start with a simple page again:

```java
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

Here `@GemText` calls a function `getAvailableCarsTitle()` to get the correct
value. The function was never declared in the component, but the generator
assumes that you will implement it and creates an empty function.

!!! note 
    You could expect to be able to write something like `@GemText(value =
    getAvailableCarsAmount() + " available cars")`, but this is not allowed. The
    generator is unable to process more complex expressions that have calls inside
    of them.

Let's add this function to `CarOverview.java`.
We can apply TOP-mechanism and override the empty function to show the number of
available cars:

```java
package gui;

import Carrental.Car;
import Carrental.RentableStatus;
import umlp.jsweet.extension.annotation.Component;

@Component()
public class CarOverview extends CarOverviewTOP{

  public String getAvailableCarsTitle() {
    if (this.getCars() == null) {
      return "";
    }
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
    Just like backend Java files (e.g., `CarrentalServerApplication.java`), frontend Java classes are placed in the `frontend/src/main/java/<packageName>/` directory.
    In this case, the only package in this tutorial used is `gui`.

The `getAvailableCarsTitle` method checks if cars are loaded and counts amount
of available cars and return a string indicating the amount, e.g. `3 available
cars`.

!!! question "Where do `this.getCars()` and `c.getRentableStatus()` methods come from?" 
    The generator produces a bunch of methods that are mapped from variables and
    attributes in class diagram and GUI component model.

Some useful generated methods:

- Each GUI component parameter has a getter, for example, in `page
  CarOverview(List<Car> cars)` the `cars` is transformed into

    - `getCars` method
    - `initCars` method
    - `setCars` method
    - `_cars` attribute
    - `cars` pseudo-attribute

- Each class attribute has a getter, for example, in `class Car { RentableStatus
  rentableStatus; }` the `rentableStatus` is transformed into

    - `getRentableStatus` method.
    - `setRentableStatus` method.
  
    !!! note 
        For `boolean` type attributes `is` prefix is generated. For example, 
        if attribute `boolean available` was defiend, `isAvailable` method would be 
        generated instead of `getAvailable`.

- If an attribute is a list or is described as association with `*` cardinality
  additional methods can be used, for example, a Car has an association
  `association [1] Car -> MileageDataPoint [*];` which is transformed into:

    - `addMileageDataPoint2` method
    - `removeMileageDataPoint2` method
    - `sizeMileageDataPoints` method
  
    !!! note 
        The `2` at the end doesn't really mean anything, it solves a specific 
        technical issue. In general, you should rely on the IDE to help you out with 
        a correct name.

The check `this.getCars() == null` in the beginning is necessary because
before the cars are loaded from the backend, the function shouldn't try to
operate with the cars data. The Angular framework evaluates this function (and
other functions that return some value shown in GUI) "continuously" to keep the
user interface up-to-date.

!!! note
    This is a common Angular trap. Whenever a not-yet-loaded object is used
    in calculations, an error will be thrown preventing GUI from operating
    normally (although it might appear to be fine).

## Adding services

Let's extend the page to show cars and a button for renting them:
```java
package gui;
/* imports */
page CarOverview(List<Car> cars) {
  @NavContainer(content = [
    @GemColumn(components = [

      @GemText(value = getAvailableCarsTitle()),
      @iterate(Car c : cars) {
        @GemText(value = c.make + " " + c.model + " " + c.name);
        @GemButton(leftClick = rentCar(c));
      }

    ])
  ]);
}
```
Here we iterate through cars and add a button for each car. The `rentCar` is a
custom function similar to `getAvailableCarsTitle`, but it expects an
`Consumer<Void>` (the TypeScript equivalent would be the `Event<void>` type) to be returned. 
The generated code is:
```java
public Consumer<Void> rentCar(Car val) {
  return null;
}
```
The generator finds out what a function should return based on context. Here,
leftClick is an event that doesn't provide any value about the left click (thus
`Consumer<Void>`).

!!! note
    The button could, for example, provide an id of a clicked button as a
    string, in which case the type would be `Consumer<String>`

The `Consumer<Void>` type actually indicates a callback function that should be
called when an event triggers, e.g., when left click happens. This means that
the function `rentCar` has to return a function. For example, we want to call a
function that changes status of the car from available to rented when the button
is clicked. Thus, we implement the following method:
```java
/*other imports*/
import java.util.function.Consumer;
/*other imports*/
public class CarOverview extends CarOverviewTOP {
  @Override
  public Consumer<Void> rentCar(Car car) {
    return x -> {
      car.setRentableStatus(RentableStatus.RENTED);
    };
  }
  /*...*/
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
/*other imports*/
import carrental.commands.RentItem;
import umlp.backendrte.command.CommandManager;
/*other imports*/
public class CarOverview extends CarOverviewTOP {
  @Override
  public Consumer<Void>  rentCar(Car car) {
    return x -> {
      CommandManager.executeCommand(new RentItem(car.getGemId()));
    };
  }
  /*...*/
}
```

### Event Handling Best Practices

Currently, method overloading is not supported for event handler methods in the GUI DSL (this might change in future versions). Instead of:
```gui
@GemButton(leftClick=doSomething("A"));
@GemButton(leftClick=doSomething("B"));
```
Use unique method names in the GUI DSL and delegate to a common implementation in Java:
```gui
@GemButton(leftClick=doThingA());
@GemButton(leftClick=doThingB());
```
```java
public Consumer<Void> doThingA() { return v -> doSomething("A"); }
public Consumer<Void> doThingB() { return v -> doSomething("B"); }
private void doSomething(String param) { ... }
```
