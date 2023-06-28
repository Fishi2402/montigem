# Text-Input

----------------------------------------------------------------------

## Goal

The goal of this project is to implement and test a new Angular component that contains an input field with an optional label. In this case, the input type can be "text", "date" or "tel". It is designed to be used in forms or other contexts where user input is required. The input field's size can be adjusted to fit the parent container.

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Overview](#overview)
- [Text-Input](#text-input)
- [Data-Structure](#data-structure)
- [GUI Model, class diagram and hand-written extension](#gui-model-class-diagram-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use this component](#how-to-use-the-text-input-component)

----------------------------------------------------------------------

## Text-Input

The Angular directive "NgModel" is used to track the value of input field and the "ngModelChange"
event to emit changes. This means that any changes made in the input field will be automatically reflected in the underlying data model. Validation logic for "errors" and "warnings" can be added to the component as well. The input field's border becomes red and an appropriate icon shows if an error occurs. The input field's border becomes yellow and a suitable sign shows up whenever a warning occurs. When an error or warning occurs, a custom message can be added to appear as a tooltip.

----------------------------------------------------------------------

## Data Structure

The data structure is defined in _gem-text-input-types.types-gen.ts_. The used data structure is _GemTextInputValidity_.

### GemPieChartData

GemTextInputValidity consists of two parameters and they are: string status and string message.

An example of _GemTextInputValidity_ can be found in the following listing 4.1:

```ts
const validityError: GemTextInputValidity = {
  status:"Error",
  message:"Error Message",
};

const validityWarning: GemTextInputValidity = {
  status:"Warning",
  message:"Warning Message",
};
```
_Listing 4.1: Example of GemTextInputValidity_

----------------------------------------------------------------------

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to add an additional class diagram model, which describes the data type. The third part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration. 

1. First create a new folder in inputs library with name _gemtextinput_. 
2. Then create the .gui file with name _GemTextInput.gui_. And add the following code: 

```
package input.src.lib.components.gemtextinput;

import input.src.lib.components.gemtextinput.GemTextInputTypes.GemTextInputValidity;


component GemTextInput(
    String id,
    String labelText,
    String inputType,
    ?String entry,
    GemTextInputValidity validity
)

```
In this file, we state the Java-like package name _gemtextinput_, import the additional data type _GemTextInputValidity_, declare the component named _GemTextInput_, and define input parameters _id, _labelText, _inputType, _entry and _validity.

### Class Diagram

For the the validation part of the component we need to create an additional class diagram to declare the additional types.
1. Create the .cd file with name _GemTextInputTypes.cd_. According to description in section [Data-Structure](#data-structure) we add the following code:

```
package input.src.lib.components.gemtextinput;

classdiagram GemTextInputTypes {

  class GemTextInputValidity {
    String status;
    String message;
  }

}

```
The additional interfaces could be generated, which can be imported and used in any component.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-text-input.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file.

2. Create a hand-written .scss file with name _gem-text-input.component.scss_.
This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-text-input.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.

```ts
import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { GemTextInput as GemTextInputTOP } from "./gem-text-input/gem-text-input.component-gen";

@Component({
  selector: "gem-text-input",
  templateUrl: "./gem-text-input.component.html",
  styleUrls: ["./gem-text-input.component.scss"],
})
export class GemTextInput extends GemTextInputTOP implements OnInit {
  //... implementation
}

```
4. Create a hand-written module file with name _gemtextinput.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file. 
For example, in this case we need to add _FormsModule_ and _MatTooltipModule_. Then we add it into the import list.

```
import { FormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";

export const imports = [FormsModule, MatTooltipModule];

```

### Export the classes

1. Create index.ts in folder _gemtextinput_, with the following content:
```
export * from "./gem-text-input.component";
export * from "./gemtextinput.module-gen"
export * from "./gem-text-input-types/gem-text-input-types.types-gen"

``` 
in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file: 
```
export * from "./lib/components/gemtextinput"
```

----------------------------------------------------------------------

## Implementation

In this section the implementation details will be discussed.

### onEntryChange
This function is an event handler that is triggered when the input field value changes. It takes a single parameter, event, which is the new value of the input field.
The function emits an entryChange event using the emit method of the _EventEmitter_ class. The entryChange event can be subscribed to by other components or services, allowing them to be notified when the value of the input field changes.



----------------------------------------------------------------------

## How to use this component

In this section will explain the possible inputs and outputs in detail and show an example
(listing 6.1). 

```ts
<gem-text-input
    id="firstName"
    labelText="First name:"
    inputType="text"
    [entry]="getFirstNameInput()"
    (entryChange)="setFirstNameInput($event)"
    [validity]="getFirstNameObj()"
  ></gem-text-input>
```

_Listing 6.1 the Example of how to use the component_

### id
This is merely the component's id, so it can be simply targeted in the styles file.
Id is also used to associate the input field with its label.

### labelText 
This is the label of the input field. This input is optional.

### inputType
This determines the type of the input field. This components is suited only for types "text", "date" and "tel".

### entry
This is the string variable that is bound to the input field using "NgModel" directive.
In this scenario, we have a getter to retrieve this variable: "getFirstNameInput()".
More detail can be found in the section [Text-Input](#text-input).

### entryChange
This is the EventEmitter; when the input changes, the EventEmitter emits the new value, and in this case the function "setFirstNameInput($event)" is called, with "$event" representing the new value.
More details can be found in the sections [Implementation](#implementation) and [Text-Input](#text-input)

### validity
This is the object that is utilized in the component's validation.
In this scenario, we have a getter to retrieve this value: "getFirstNameObj()".
More details can be found in the section [Text-Input](#text-input) and [Data-Structure](#data-structure)



----------------------------------------------------------------------
