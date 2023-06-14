# GemRow

---

## Overview

- [GemCard](#GemRow)
- [GUI Model](#gui-model)
- [Implementation](#implementation)

## GemRow

GemRow is a container which arranges any number of input elements horizontally

<img src="./images/RowWireframe.PNG" alt="Row Layout" width=50%>

---

## GUI Model

```
package arrange.src.lib.gemrow;

component GemRow(
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
- `List<GUIViewElement> components` - components to display inside a row

Usage example:

```
page PersonOverview(Person p) {
  @GemRow(
    components = [
      @GemTextInput(value = p.name),
      // ...
      @GemTextInput(value = p.phone)
    ]
  );
}
```

Here `Person` is a class describing a person.
`GemRow` takes a list of components as an input and displays it inside its body.
In the example text inputs are given as the input components.

<img src="./images/Row.PNG" alt ="Rows Layout" width=100%>
