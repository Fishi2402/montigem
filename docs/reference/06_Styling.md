# 6. Styling & Theming

MontiGem uses SCSS for styling.

## 6.1 Component Styles

Create a `.scss` file with the same name as your `.gui` file in the same directory (e.g., `CarOverview.scss`). Styles defined here are scoped to the component.

## 6.2 Targeting Components

Assign an identifier to a component instance in the GUI model to style it specifically.

**Model:**
```java
primaryButton@GemButton(label="Save");
```

**SCSS:**
The identifier becomes a CSS class.
```scss
.primaryButton .gem-button {
  background-color: green;
  color: white;
}
```

## 6.3 Deep Styling

To style elements inside a child component (e.g., inside a `GemCard`), you often need to pierce Angular's view encapsulation using `::ng-deep`.

```scss
:host ::ng-deep .gem-card-title {
  color: blue;
}
```

## 6.4 GUI Design Patterns

For best practices on structuring your GUI and applying styles consistently, see [GUI Design Patterns](../designpatterns/GUIPatterns.md).

---
[< Previous: RTE & Synchronization](05_RTE_Synchronization.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: Backend Customization >](07_Backend.md)
