# Button

---

## Goal

The goal of this project is to implement and test a new Angular library, in this case is button component. This component should be customizable with source, and is tested using unit-tests.

---

## Overview

- [Goal](#goal)
- [Overview](#overview)
- [Button](#button)
- [GUI Model](#gui-model)
- [Implementation](#implementation)
- [How to use the Audio Component and Inputs](#how-to-use-the-audio-component-and-inputs)
- [References](#references)

---

## Button

The button component is a link to trigger an action. It can be used to trigger a function, or to link to another page. More information could be found in [1].

<img src="https://cdn.pixabay.com/photo/2016/02/08/19/52/button-1187460_1280.png" alt="ButtonComponent" width=50%>

_Examples of button component_

---

## GUI Model

In this section discuss about the GUI model, which is the typical component declaration.

1. First create a new folder in inout library with name _gembutton_.
2. Then create the .gui file with name _GemButton.gui_. And add the following code:

```
package basic.src.lib.gembutton;

component GemButton(
  String label,
  List<String> classes,
  Object style,
  Boolean disabled
)

```

In this file, we state the Java-like package name _gembutton_, declare the component named _GemButton_, and define input parameter _source_.

---

## Implementation

In this section the implementation details will be discussed.

1. Create a .html file named _gem-button.component.html_, with the following code:

```
<button
  type="button"
  role="button"
  class="gem-button"
  [ngStyle]="style"
  [ngClass]="classes"
  [disabled]="disabled ? 'disabled' : null"
>
  {{ label }}
</button>
```

By using this hand-written HTML file, the generated one will automally point to this one as its template.

---

## How to use the Button Component and Inputs

In this section will show how to use the Button Component and explain the input.

**Note that it is highly recommended to put the Button Component into a div-element.**

```html
<div fxLayout fxLayoutGap="20px">
  <gem-button
    label="class: blue-green-transition"
    [classes]="'blue-green-transition'"
  >
  </gem-button>
  <!--other components-->
</div>
```

_Listing 6.1 the Example of usage of button component and input_

### source

Specify the source of the button file.

---

## References

[1] _Building a button component_ https://web.dev/building-a-button-component/ Accessed: 2023-04-21.
