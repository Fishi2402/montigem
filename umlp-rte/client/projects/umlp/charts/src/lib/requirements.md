# Requirements for Chart Modeling
----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [GUI Model and Class Diagram](#gui-model-and-class-diagram)
- [The Component](#the-component)
- [Chart Options](#chart-options)
- [Chart Legend](#chart-legend)
- [Chart Example](#chart-example)
- [Documentation](#documentation)

----------------------------------------------------------------------

## Goal

The goal of using the following requirements is to achieve a consistency between all graphs and their respective models,
as well as their source code.
Of course it is impossible to make all charts behave the same, but they can feel consistent enough to feel the same,
making them easier to use and to implement. For example purposes, _GemLineChart_ will be taken as an example.

----------------------------------------------------------------------

## GUI Model and Class Diagram

The GUI Model is created inside of the component folder _gemlinechart_ and models the chart parameters defined in the _component.ts_ file.

- The GUI model name consists of a _Gem_ prefix, followed by the chart name _LineChart_ in camelCase.
- The input parameters of the GUI Model equal the input parameters of the component
- The attribute for the input data has to be named _data_

```
package charts.src.lib.gemlinechart;

import charts.src.lib.gemlinechart.GemLineChartTypes.GemLineChartData;

component GemLineChart(
  GemLineChartData data,
  Object displayValue,
  Boolean enableBackgroundColor,
  Integer maxValue,
  Integer minValue
)
```


Chart components make use of complex data types, so a class diagram is needed.

- The name of the _classdiagram_ has a _Gem_ prefix, the chart name _LineChart_ and suffix _Types_.\
The class used for the data attribute has the suffix _Data_. If the subclass has a list of data entries, the name of the subclass is the same, with the suffix being replaced by _Entry_.
- If possible, entry names are named _label_ with value _data_, if the data is a list of elements. If not, then _value_ is needed instead of _data_, as some charts encounter problems otherwise.

```
package charts.src.lib.gemlinechart;

classdiagram GemLineChartTypes {

  class GemLineChartData {
    List<GemLineChartEntry> entries;
    List<String> labels;
  }
 
  class GemLineChartEntry {
    String label;
    List<Integer> data;
    String borderColor;
    String backgroundColor;
  }

}

```

----------------------------------------------------------------------

## The Component

### HTML
- Options are displayed using the `<chart-options>`-element, where each option group is in its own respective `<li>` element. Inside the `<chart-options`> element, use the `<h3>` element to specify the title of the option group.
- The Legend is displayed using the `<gem-chart-legend>`-element.
- The graph has its own container, as well as the chart and its legend.
- Inputs and their respective parameter should have the same name for simplicity.
- If there are inputs that can change the whole chart, for instance `[chartType]`, then the input is hard coded to minimize user errors.

### SCSS
- Container and various other styles are defined here, not in the _component.ts_.

### TypeScript
- The generated component extends the base component according to the rules.
- Generated data types are used for the input data.
- To manually set a default value, define a new variable with the same name but with a "_"-prefix.
- Additional attributes are needed for the legend. They are named _legendLabels_ and _legendColors_ and contain the values needed for the legend. 
- If no color data has been given to the component, then the colors for a chart entry must be taken from _[colors.ts](colors.ts)_.

----------------------------------------------------------------------
 
## Chart Options
Configurations of a chart are displayed inside of an overlay, which opens by pressing the gear button provided by the component. This component is used by other components like _GemLineChart_ to display settings with the `<ng-content>` element. Styles are automatically applied to the options.



```html
<button (click)="buttonClicked = !buttonClicked" type="button" cdkOverlayOrigin #trigger="cdkOverlayOrigin" style="font-size: 15px">
  <i class="fa fa-gear"></i>
</button>

<ng-template
  cdkConnectedOverlay
  [cdkConnectedOverlayOrigin]="trigger"
  [cdkConnectedOverlayOpen]="buttonClicked"
  >
  <ul class="settings-list">
    <ng-content></ng-content>
  </ul>
</ng-template>
```

----------------------------------------------------------------------

## Chart Legend
The legend of a chart has its own component and can be used by all charts.
The component has three attributes, namely _colors_, _labels_ and _displayLegend_. To note is that the _colors_ function, as well as the data structure of _entryColors_ must be updated, if a new chart requires additional color attributes.

```typescript
  // ...

  @Input()
  public set labels(labels: string[]) {
    this._labels = labels;
  }

  @Input()
  public set colors(entryColors: {borderColor?:string, backgroundColor: string}[]) {
    this._entries = [];

    if (entryColors[0].borderColor != null ) {
      this._colors = entryColors.map((entry) => entry.borderColor);
    }
    else {
      this._colors = entryColors.map((entry) => entry.backgroundColor);
    }

    for (let i = 0; i < this._labels.length; i++) {
      let legendEntry = {
        label: this._labels[i],
        color: this._colors[i],
      };
      this._entries.push(legendEntry);
    }
  }
```

----------------------------------------------------------------------

## Chart Example
In the _component.ts_ file, the data and several possible options are defined.

- The class name consists of the component name and the suffix _ExampleComponent_, resulting in the class name _GemLineChartExampleComponent_.
- The name of the input parameters equal the names of the defined attributes and options.
```ts
export class GemLineChartExampleComponent {
  title = "gem-line-chart";
  data = data;
  enableBackgroundColor = enableBackgroundColor;
  maxValue = maxValue;
  minValue = minValue;
  displayValue = displayValue;
}
```

- The data makes use of the data types that are provided by the generated interface of the class diagram, and options are set to the desired way.

```ts
import { GemLineChartEntry, GemLineChartData } from "@monticore/montigem-charts";
//...
const entries: GemLineChartGroup[] = [
  { data: [132.54, 136.96, 153.65], label: 'Label 1', backgroundColor: "rgba(246, 168, 0, 0.2)" },
  { data: [252.18, 271.60, 301.88], label: 'Label 2', backgroundColor: "rgba(246, 168, 0, 0.2)" },
  { data: [138.82, 134.14, 139.24], label: 'Label 3', backgroundColor: "rgba(246, 168, 0, 0.2)" }
];

const labels: string[] = ['January', 'February', 'March'];

const data: GemDataLineChart = {
  entries,
  labels,
}

const enableBackgroundColor = true;
const maxValue = 340;
const minValue = 50;
const displayValue = a => `${a} $`;
```

- The chart component is inside a `<div>` element with the class _example_, to set the height and width of the graph.
```ts
<div class="example">
  <gem-line-chart 
    [data]="data"
    [maxValue]="maxValue"
    [minValue]="minValue"
    [displayValue]="displayValue"
    [enableBackgroundColor]="enableBackgroundColor"
    >
  </gem-line-chart>
</div>
```

----------------------------------------------------------------------

## Documentation
The documentation of a chart is necessary to help the user understand the chart better, as well as enhance further development. The documentation is inside a _doc_ folder of the corresponding chart, with the subfolder _images_ if images are needed.


The documentation should contain the following steps: 

- **Table of Contents**\
Show the structure of the documentation.

- **Description**\
Give a description of the chart.

  - What is the given Chart?
  - What is it for?
  - How does it show information?
  - Example images

- **Data Structure**
  - Explain the data structure of the chart
  - include your Class Diagram Model, e.g., _GemBarChartTypes.cd_

- **GUI Model**
  - Implementation details

- **Implementation**
  - Handwritten file extensions
  - How to export the component for use


- **How to use the Chart and Inputs**\
Give an Example for each input.

  - Show which type the input is of
  - Description of input
  - Example HTML

- **Source**

- **References**\
Give references for images and other here.
