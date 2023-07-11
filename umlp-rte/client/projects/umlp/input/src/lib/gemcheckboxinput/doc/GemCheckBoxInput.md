# Checkbox-Input

---

## Goal

The goal of this project is to implement and test a new Angular component that contains a checkbox with a label. It is designed to be used in forms or other contexts where the user needs to indicate a binary choice, such as agreeing to terms and conditions or opting in to a newsletter. A single instance of the component results in a single checkbox input.

---

## Overview

- [Goal](#goal)
- [Overview](#overview)
- [Checkbox-Input](#checkbox-input)
- [Data-Structure](#data-structure)
- [GUI Model and hand-written extension](#gui-model-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use this component](#how-to-use-the-checkbox-input-component)

---

## Checkbox-Input

To link the value of the checkbox input to a boolean property, the Angular directive "NgModel" is utilized. This allows us to quickly track the checkbox's state and use it in other parts of an application.
When the user clicks on the checkbox, the "ngModelChange" event is utilized to broadcast the changed boolean value of the checkbox input. Other components of an application can then respond to changes in the checkbox status by updating their own state.

---

## Data Structure

This component does not require any additional data structures to work.

---

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration.

1. First create a new folder in inputs library with name _gemcheckboxinput_.
2. Then create the .gui file with name _GemCheckBoxInput.gui_. And add the following code:

```
package input.src.lib.components.gemcheckboxinput;

component GemCheckBoxInput(
  String id,
  String labelText,
  String value,
  ?Boolean checked
)
```

In this file, we state the Java-like package name _gemcheckboxinput_, declare the component named _GemCheckBoxInput_, and define input parameters \_id, \_labelText, \_value and \_checked.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-check-box-input.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file.

2. Create a hand-written .scss file with name _gem-check-box-input.component.scss_.
   This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-check-box-input.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.

```ts
import { Component, OnInit, EventEmitter, Output } from "@angular/core";
import { GemCheckBoxInput as GemCheckBoxInputTOP } from "./gem-check-box-input/gem-check-box-input.component-gen";

@Component({
  selector: "gem-checkbox-input",
  templateUrl: "./gem-check-box-input.component.html",
  styleUrls: ["./gem-check-box-input.component.scss"],
})
export class GemCheckBoxInput extends GemCheckBoxInputTOP implements OnInit {
  //... implementation
}

```

4. Create a hand-written module file with name _gemcheckboxinput.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file.
   For example, in this case we need to add _FormsModule_. Then we add it into the import list.

```
import { FormsModule } from "@angular/forms";

export const imports = [
  FormsModule
];

```

### Export the classes

1. Create index.ts in folder _gemcheckboxinput_, with the following content:

```
export * from "./gem-check-box-input.component";
export * from "./gemcheckboxinput.module-gen";

```

in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file:

```
export * from "./lib/components/gemcheckboxinput"
```

---

## Implementation

In this section the implementation details will be discussed.

### onCheckedChange
This function is an event handler that is called when the checkbox's boolean value changes. It only accepts one parameter, event, which is the boolean value that has been modified.
Using the emit method of the _EventEmitter_ class, the function broadcasts a checkedChange event. Other components or services can listen to the checkedChange event to be notified when the boolean value of the checkbox changes.

---

## How to use this component

In this section will explain the possible inputs and outputs in detail and show an example
(listing 6.1).

**Note that it is highly recommended to put multiple checkboxes in a div-element.**

```ts
<div class="checkboxDiv">
  <gem-checkbox-input
    id="checkbox1"
    labelText="Checkbox 1: "
    value="one"
    [checked]="getCheckbox1()"
    (checkedChange)="setCheckbox1($event)"
  ></gem-checkbox-input>
  <gem-checkbox-input
    id="checkbox2"
    labelText="Checkbox 2: "
    value="two"
    [checked]="getCheckbox2()"
    (checkedChange)="setCheckbox2($event)"
  ></gem-checkbox-input>
</div>
```

_Listing 6.1 the Example of how to use the component_

### id
This is merely the component's id, so it can be simply targeted in the styles file.
Id is also used to associate the checkbox with its label.

### labelText
This is the checkbox label, which also acts as an explanation of the checkbox's value, or it can be the same as the value.

### value
This is the string value that the checkbox holds.

### checked
This is the boolean variable that is bound to the checkbox using "NgModel" directive.
In this scenario, we have a getters to retrieve this variable: "getCheckbox1()" and "getCheckbox2()".
More detail can be found in the section [Checkbox-Input](#checkbox-input).

### checkedChange
This is the EventEmitter; when the value of the checkbox changes, it emits the new boolean value, and in this case the functions "setCheckbox1($event)" and "setCheckbox2($event)" are called, with "$event" being the new boolean value.
More details can be found in the sections [Implementation](#implementation) and [Checkbox-Input](#checkbox-input)

---
