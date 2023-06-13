# Textarea-Input

----------------------------------------------------------------------

## Goal

The goal of this project is to implement and test a new Angular component that contains a text area with an optional label. It is designed to be used in forms or other contexts where multi-line user text input is required. By clicking and dragging the mouse on the text area's bottom right corner, the size of the text area can be readjusted.

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Overview](#overview)
- [Textarea-Input](#textarea-input)
- [Data-Structure](#data-structure)
- [GUI Model and hand-written extension](#gui-model-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use this component](#how-to-use-the-textarea-input-component)

----------------------------------------------------------------------

## Textarea-Input

The Angular directive "NgModel" is used to track the value of text area and the "ngModelChange"
event to emit changes. This means that any changes made in the text area will be automatically reflected in the underlying data model.

----------------------------------------------------------------------

## Data Structure

This component does not require any additional data structures to work.


----------------------------------------------------------------------

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration. 

1. First create a new folder in inputs library with name _gemtextareainput_. 
2. Then create the .gui file with name _GemTextareaInput.gui_. And add the following code: 

```
package input.src.lib.components.gemtextareainput;

component GemTextareaInput(
  String labelText,
  ?String textArea,
  String id
)

```
In this file, we state the Java-like package name _gemtextareainput_, declare the component named _GemTextareaInput_, and define input parameters _id, _labelText and _textArea.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-textarea-input.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file.

2. Create a hand-written .scss file with name _gem-textarea-input.component.scss_.
This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-textarea-input.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.

```ts
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { GemTextareaInput as GemTextareaInputTOP } from './gem-textarea-input/gem-textarea-input.component-gen';

@Component({
  selector: 'gem-textarea-input',
  templateUrl: './gem-textarea-input.component.html',
  styleUrls: ['./gem-textarea-input.component.scss']
})
export class GemTextareaInput extends GemTextareaInputTOP implements OnInit {
  //... implementation
}

```
4. Create a hand-written module file with name _gemtextareainput.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file. 
For example, in this case we need to add _FormsModule_. Then we add it into the import list.

```
import { FormsModule } from "@angular/forms";

export const imports = [
  FormsModule
];

```

### Export the classes

1. Create index.ts in folder _gemtextareainput_, with the following content:
```
export * from "./gem-textarea-input.component";
export * from "./gemtextareainput.module-gen";


``` 
in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file: 
```
export * from "./lib/components/gemtextareainput"
```

----------------------------------------------------------------------

## Implementation

In this section the implementation details will be discussed.

### onTextAreaChange
This function is an event handler that is triggered when the value in the text area changes. It takes a single parameter, event, which is the new value in the text area.
The function emits a textAreaChange event using the emit method of the _EventEmitter_ class. The textAreaChange event can be subscribed to by other components or services, allowing them to be notified when the value in the text area changes.


----------------------------------------------------------------------

## How to use this component

In this section will explain the possible inputs and outputs in detail and show an example
(listing 6.1). 

```ts
<gem-textarea-input
  id="textarea"
  labelText="Label: "
  [textArea]="getTextAreaInput()"
  (textAreaChange)="setTextAreaInput($event)"
></gem-textarea-input>
```

_Listing 6.1 the Example of how to use the component_

### id
This is merely the component's id, so it can be simply targeted in the styles file.
Id is also used to associate the text area with its label.

### labelText 
This is the label of the text area. This input is optional.

### textArea
This is the string variable that is bound to the text area using "NgModel" directive.
In this scenario, we have a getter to retrieve this variable: "getTextAreaInput()".
More detail can be found in the section [Textarea-Input](#textarea-input).

### textAreaChange
This is an EventEmitter; when the input changes, the EventEmitter emits the new value, and in this case the function "setTextAreaInput($event)" is called, with "$event" representing the new value.
More details can be found in the sections [Implementation](#implementation) and [Textarea-Input](#textarea-input)

----------------------------------------------------------------------
