# GemColumn

---

## Overview

- [GemColumn](#gem-column)
- [GUI Model](#gui-model)
- [Implementation](#implementation)

## GemColumn

GemColumn is a container which arranges any number of input elements vertically

<img src="./images/ColumnWireframe.PNG" alt="Column Layout" width=50%>

---

## GUI Model

```
package arrange.src.lib.gemcolumn;

component GemColumn(
  String height,
  String width,
  String hAlign,
  String vAlign,
  List<GUIViewElement> components,
)
```

Inputs:

- `String height` - height of the component in `px`, `%`, `vh`, etc.
- `String width` - width of the component in `px`, `%`, `vh`, etc.
- `String hAlign` - horizontal alignment of the embedded components.
- `String vAlign` - vertical alignment of the embedded components.
- `List<GUIViewElement> components` - components to display inside a column

Usage example:

```
page PersonOverview(Person p) {
  @GemColumn(
    components = [
      @GemTextInput(value = p.name),
      // ...
      @GemTextInput(value = p.phone)
    ]
  );
}
```

Here `Person` is a class describing a person.
`GemColumn` takes a list of components as an input and displays it inside its body.
In the example text inputs are given as the input components.

<img src="./images/Column.PNG" alt ="Columns Layout" width=50%>
