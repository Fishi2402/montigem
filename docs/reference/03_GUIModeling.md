# 3. GUI Modeling (GUI DSL)

The user interface is defined in `.gui` files.

## 3.1 Components and Pages

*   **`component Name(...)`**: Defines a reusable UI part.
*   **`page Name(...)`**: Defines a root component accessible via a URL route.

**Structure:**
```java
package mypackage;

import external.Component;

component MyComponent(String title, boolean isVisible) {
  /* content */
}
```

**Parameters:**
Parameters defined in the component signature become **Inputs** in Angular.
*   `title`: Available as `this.getTitle()` in Java HWC.
*   `isVisible`: Available as `this.isIsVisible()` (boolean convention).

## 3.2 Component Usage

Components are instantiated using the `@` syntax inside another component's body.

```java
@MyComponent(
  title = "Hello World",
  isVisible = true
);
```

*   **Named Arguments:** Arguments must be passed by name (`param = value`).
*   **Identifiers:** An instance can be given a name: `myInstance@MyComponent(...)`. This name is used as a CSS class and for referencing the component in the HWC.

## 3.3 Layout Components

MontiGem provides standard layout components in `mc.fenix.arrange`.

#### `GemColumn`
Arranges children vertically (Flexbox column).
```java
@GemColumn(
  rowGap = "10px",
  width = "50%",
  hAlign = "center", // left, center, right
  components = [ @GemText(...), @GemButton(...) ]
);
```

#### `GemRow`
Arranges children horizontally (Flexbox row).
```java
@GemRow(
  colGap = "20px",
  vAlign = "bottom", // top, center, bottom
  wrap = "wrap",     // wrap, nowrap
  components = [ ... ]
);
```

#### `GemGrid`
Arranges children in a 2D grid.
```java
@GemGrid(
  rows = [
    [ @GemText("Row 1, Col 1"), @GemText("Row 1, Col 2") ],
    [ @GemText("Row 2, Col 1"), @GemText("Row 2, Col 2") ]
  ]
);
```

#### `GemCard`
A container with a title and content area.
```java
@GemCard(
  title = "My Card",
  component = @GemText("Content")
);
```

## 3.4 Control Structures

#### `@iterate`
Renders the block for each element in a list.
```java
@iterate(Car c : cars) {
  @GemText(value = c.model);
}
```
*   Automatically updates when the list changes.
*   **Note:** If used inside a `components = [...]` list, it does **not** require a semicolon.

#### `@guard`
Renders the block only if the condition is true.
```java
@guard(c.price > 1000) {
  @GemText(value = "Expensive", color = "red");
} else {
  @GemText(value = "Cheap", color = "green");
}
```
*   `condition` is a Java boolean expression.
*   **Syntax Note:** If used inside a `components = [...]` list, the syntax is strict. Use semicolons inside the block or separate guards with commas.
    ```java
    components = [
      @guard(cond) { @Comp(...); }, // Semicolon inside
      @guard(!cond) { @Comp(...); }
    ]
    ```

## 3.5 Navigation

*   **URLs:** A page `gui.CarUpdate` is accessible at `/gui/CarUpdate`.
*   **Parameters:** If a page takes a domain object as a parameter (e.g., `page CarUpdate(Car car)`), the URL includes the object's ID: `/gui/CarUpdate/:carId`.
*   **Router:** Use `umlp.guiutil.Router.navigate(...)` in Java HWC to navigate.
    ```java
    Router.navigate("gui", "CarUpdate", String.valueOf(car.getGemId()));
    ```

## 3.6 Generating GUIs from Data Models (CD2GUI)

For rapid prototyping, MontiGem can generate basic CRUD (Create, Read, Update, Delete) views directly from your class diagram.

*   **Functionality:** Generates list views, detail views, and edit forms.
*   **Usage:** This is a powerful feature for quickly getting a working UI, which can then be customized.

For a detailed guide on how to use this feature, see [Class Diagram to GUI](../unstructured/CD2GUI.md).

---
[< Previous: Data Modeling](02_DataModeling.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: Frontend Logic >](04_FrontendLogic.md)
