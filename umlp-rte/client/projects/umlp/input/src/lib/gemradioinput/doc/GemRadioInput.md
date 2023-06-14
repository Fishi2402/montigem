# Radio-Input

---

## Goal

The goal of this project is to implement and test a new Angular component that contains a radio button with a label. It is intended for use in forms or other scenarios where users must choose one of several possibilities. Because a single instance of the component results in a single radio button, more than one instance is required to construct a radio group.

---

## Overview

- [Goal](#goal)
- [Overview](#overview)
- [Radio-Input](#radio-input)
- [Data-Structure](#data-structure)
- [GUI Model and hand-written extension](#gui-model-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use this component](#how-to-use-the-radio-input-component)

---

## Radio-Input

The Angular directive "NgModel" is used to bind the value of the radio button to a property. This allows us to keep track of the selected value of the radio group and update it when the user selects a different option.
The "ngModelChange" event is used to emit changes to the selected value when the user clicks on a radio button.

---

## Data Structure

This component does not require any additional data structures to work.

---

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration.

1. First create a new folder in inputs library with name _gemradioinput_.
2. Then create the .gui file with name _GemRadioInput.gui_. And add the following code:

```
package input.src.lib.components.gemradioinput;

component GemRadioInput(
  String id,
  String labelText,
  String value,
  ?String selectedValue,
  Boolean isChecked
)

```

In this file, we state the Java-like package name _gemradioinput_, declare the component named _GemRadioInput_, and define input parameters \_id, \_labelText, \_value, \_selectedValue, \_isChecked.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-radio-input.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file.

2. Create a hand-written .scss file with name _gem-radio-input.component.scss_.
   This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-radio-input.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.

```ts
import { Component, OnInit, Output, EventEmitter } from "@angular/core";
import { GemRadioInput as GemRadioInputTOP } from "./gem-radio-input/gem-radio-input.component-gen";

@Component({
  selector: "gem-radio-input",
  templateUrl: "./gem-radio-input.component.html",
  styleUrls: ["./gem-radio-input.component.scss"],
})
export class GemRadioInput extends GemRadioInputTOP implements OnInit {
  //... implementation
}
```

4. Create a hand-written module file with name _gemradioinput.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file.
   For example, in this case we need to add _FormsModule_. Then we add it into the import list.

```
import { FormsModule } from "@angular/forms";

export const imports = [
  FormsModule
];

```

### Export the classes

1. Create index.ts in folder _gemradioinput_, with the following content:

```
export * from "./gem-radio-input.component";
export * from "./gemradioinput.module-gen";

```

in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file:

```
export * from "./lib/components/gemradioinput"
```

---

## Implementation

In this section the implementation details will be discussed.

### onSelectedValueChange
This function is an event handler that is called when the selected value of the radio button group changes. It only accepts one parameter, newValue, which is the updated selected value.
The function emits a selectedValueChange event using the emit method of the _EventEmitter_ class. The selectedValueChange event can be subscribed to by other components or services, allowing them to be notified when the selected value of the radio button group.

---

## How to use this component

In this section will explain the possible inputs and outputs in detail and show an example
(listing 6.1).

**Note that it is highly recommended to put the radio button group into a div-element.**

```ts
<div class="genderDiv">
  <gem-radio-input
    id="genderMale"
    value="male"
    labelText="Male"
    [selectedValue]="getGenderInput()"
    [isChecked]="getGenderInput() === 'male'"
    (selectedValueChange)="setGenderInput($event)"
  ></gem-radio-input>
  <gem-radio-input
    id="genderFemale"
    value="female"
    labelText="Female"
    [selectedValue]="getGenderInput()"
    [isChecked]="getGenderInput() === 'female'"
    (selectedValueChange)="setGenderInput($event)"
  ></gem-radio-input>
</div>
```

_Listing 6.1 the Example of how to use the component_

### id
This is merely the component's id, so it can be simply targeted in the styles file.
Id is also used to associate the radio button with its label.

### labelText
This is the radio button's label, which also serves as an explanation of the radio button's value, or it can be the same as the value.

### value
This is the string value that the radio button holds.

### selectedValue
This is the string variable that is bound to the radio button using "NgModel" directive.
It is critical that every radio button in the same group is tied to the same variable. This ensures that there is only one radio button value being currently selected.
In this scenario, we have a getter to retrieve this variable: "getGenderInput()".
More detail can be found in the section [Radio-Input](#radio-input).

### isChecked
This is a boolean value that is "true" for a radio button if it is currently chosen and "false" otherwise.

### selectedValueChange
This is the EventEmitter; it emits the new value when the selected value of the radio button group changes, and in this case the function "setGenderInput($event)" is called, with "$event" being the new value.
More details can be found in the sections [Implementation](#implementation) and [Radio-Input](#radio-input)

---
