# 10. How-To: Build a Master-Detail View

This guide shows how to create a common application pattern: a list of items (master) where each item links to an editable detail page.

**Scenario:** We have a list of `Car` objects and want to edit each one on a separate page.

**1. Master Page (`CarOverview.gui`)**

This page displays a list of all cars. It receives `List<Car> cars` from the backend automatically.

```java
package gui;

import mc.fenix.basic.GemText;
import mc.fenix.basic.GemButton;
import mc.fenix.arrange.GemColumn;
import Carrental.Car;

page CarOverview(List<Car> cars) {
  @GemColumn(components = [
    @iterate(Car c : cars) {
      @GemRow(components = [
        @GemText(value = c.make + " " + c.model),
        @GemButton(label = "Edit", leftClick = editCar(c))
      ]);
    }
  ]);
}
```

**2. Detail Page (`CarUpdate.gui`)**

This page displays a form for a single `Car` object. The `Car` is identified by its ID in the URL.

```java
package gui;

import Carrental.Car;
import mc.fenix.input.GemTextInput;
import mc.fenix.arrange.GemColumn;
import mc.fenix.basic.GemButton;

page CarUpdate(Car car) {
  @GemColumn(components = [
    @GemTextInput(labelText = "Model:", entry = car.model),
    @GemTextInput(labelText = "Name:", entry = car.name),
    @GemButton(label = "Back", leftClick = goBack())
  ]);
}
```
*   The `entry = car.model` creates a two-way data binding. Changes in the input field are automatically written back to the `car` object.

**3. Navigation Logic (`CarOverview.java`)**

Implement the event handler to navigate to the detail page.

```java
package gui;

import umlp.guiutil.Router;
import java.util.function.Consumer;

public class CarOverview extends CarOverviewTOP {
  public Consumer<Void> editCar(Car car) {
    return event -> {
      Router.navigate("gui", "CarUpdate", String.valueOf(car.getGemId()));
    };
  }
}
```
*   `Router.navigate` constructs the URL `/gui/CarUpdate/123` (where 123 is the car's ID).

**4. Back Navigation (`CarUpdate.java`)**

Implement the event handler to navigate back.

```java
package gui;

import umlp.guiutil.Location;
import java.util.function.Consumer;

public class CarUpdate extends CarUpdateTOP {
  public Consumer<Void> goBack() {
    return event -> {
      Location.back();
    };
  }
}
```
*   `Location.back()` uses the browser's history to go back to the previous page.

---
[< Previous: Project Setup](09_ProjectSetup.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: Further Reading >](11_FurtherReading.md)
