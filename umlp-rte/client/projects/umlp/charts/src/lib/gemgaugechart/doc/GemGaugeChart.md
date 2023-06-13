# GaugeChart

----------------------------------------------------------------------

## Goal

The goal of this project was to implement and test a new Angular library which contains a chart, in this case a gauge-chart, using ngx-charts library. The chart should be customizable using different inputs and data structure. The functions used in the component were tested with unit-tests.

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Overview](#overview)
- [Gauge-Charts](#gauge-charts)
- [Data-Structure](#data-structure)
- [GUI Model, class diagram and hand-written extension](#gui-model-class-diagram-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use the Chart and Inputs](#how-to-use-the-chart-and-inputs)
- [References](#references) 

----------------------------------------------------------------------

## Gauge-Charts

Gauge charts are also known as dial charts or speedometer charts. Normally they consist of a dial and a pointer, use the pointer to read the information. There are several different types of gauge charts, such as Speedometer, Linear Gauge Charts and Angular Gauge Charts. Gauge charts are very helpful to visualize and compare single data value with a quantitative context. An example of gauge charts can be seen in following figures and detailed information can be found in [1].

<img src="https://www.datapine.com/documentation/wp-content/uploads/2016/07/solid-angular-gauge-chart.png" alt="AngularGaugeCharts" width=80%>

_An example of solid gauge charts and angular gauge charts. Source: [2]_

<img src="https://raw.githubusercontent.com/atefeh-dev/gauge-chart/main/gauge.png" alt="LinearGaugeChart" width="80%">

_An example of linear gauge chart. Source: [3]._ 

----------------------------------------------------------------------

## Data Structure

The data structure is defined in _gem-gauge-chart-types.types-gen.ts_. The used data structure is _GemGaugeChartData_.

### GemGaugeChartData

GemGaugeChartData consists of a parameter _entries_, an array of type _GemGaugeChartEntry_. _GemGaugeChartEntry_ elements consist of three parameters, they are: string _name_, integer _value_ and string _type_.

An example of _GemGaugeChartData_ can be found in the following listing 4.1:

```ts
const entries : GemGaugeChartEntry[] = [
  {
    name: 'living room',
    value: 20,
    type: 'CO2'
  },
  {
    name: 'bedroom',
    value: 50,
    type: 'CO2'
  },
  {
    name: 'outside',
    value: -10,
    type: 'CO2'
  }
];

const dummy : GemGaugeChartData = {
  entries
};

```
_Listing 4.1: Example of GemGaugeChartData_

----------------------------------------------------------------------

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to add an additional class diagram model, which describes the data type of gauge chart. The third part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration. 

1. First create a new folder in charts library with name _gemgaugechart_. 
2. Then create the .gui file with name _GemGaugeChart.gui_. And add the following code: 

```
package charts.src.lib.gemgaugechart;

import charts.src.lib.gemgaugechart.GemGaugeChartTypes.GemGaugeChartData;

component GemGaugeChart (
  GemGaugeChartData data,
  Integer min,
  Integer max,
  String unit,
  String textValue
)

```
In this file, we state the Java-like package name _gemgaugechart_, import the additional data type _GemGaugeChartData_, declare the component named _GemGaugeChart_, and define input parameters _data, min, max, unit_ and _textValue_. 

### Class Diagram

For the complex components such as charts we need to create an additional class diagram to declare the additional types.
1. Create the .cd file with name _GemGaugeChartTypes.cd_. According to description in section [Data-Structure](#data-structure) we add the following code:

```
package charts.src.lib.gemgaugechart;

classdiagram GemGaugeChartTypes {
  class GemGaugeChartData {
    List<GemGaugeChartEntry> entries;
  }
    
  class GemGaugeChartEntry {
    String type;
    String name;
    Integer value;
  }

}
```

The additional interfaces could be generated, which can be imported and used in any component.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-gauge-chart.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file. For the code detail could find more information here [4].

2. Create a hand-written .scss file with name _gem-gauge-chart.component.scss_.
This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-gauge-chart.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.
```ts
import { GemGaugeChartData, GemGaugeChartEntry } from './gem-gauge-chart-types/gem-gauge-chart-types.types-gen';
import { GemGaugeChart as GemGaugeChartTOP } from './gem-gauge-chart/gem-gauge-chart.component-gen';

@Component({
  selector: 'gem-gauge-chart',
  templateUrl: './gem-gauge-chart.component.html',
  styleUrls: ['./gem-gauge-chart.component.scss']
})
export class GemGaugeChart extends GemGaugeChartTOP {
    //....implementations
    }

```
4. Create a hand-written module file with name _gemgaugechart.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file.

### Export the classes

1. Create index.ts in folder _gemgaugechart_, with the following content:
```
export * from "./gemgaugechart.module-gen";
export * from "./gem-gauge-chart.component";
export * from "./gem-gauge-chart-types/gem-gauge-chart-types.types-gen";
``` 

in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file: 
```
export * from "./lib/gemgaugechart";
```

----------------------------------------------------------------------

## Implementation

In this section the implementation details will be discussed, how the data is processed and transformed.

### Transformation to ngx-charts-gauge

In the _gem-gauge-chart-component.html_ file a div with a gauge-chart of ngx-charts is used. Every time an input gets updated, then the function hasData() inside _gem-gauge-chart.component.ts_ is called. This function will check if there is a non-empty dataset and the length of it is bigger than 0, if yes then it will return true. Then function showAxis() is called, which will return true by the default setting. 

Also there are some default setting for the parameters such as
_[scheme]_ with the colorScheme which defines the a domain for colors,
_[legend]_ with the true, _[legendPosition]_ with below. _[showText]_ will call the function showText(), it will return true if the textValue is not empty. _[min]_, _[max]_, _[textValue]_ will be set manually when use the component. 


### Data Processing

One of the most important part is to process the data. First of all
the data is read from the GemGaugeChartData. Then need to set the SensorType from extern to filter the data. Next use function updateSelectedDataset() to only show the selected datatype. 

The function recalcBigTicks() is used to calculate the count of the big ticks, change the count to a multiple of 10 of 100 depending on the data. The function smallTickResizer() is used to make sure the input range to select smallTicksCount(). Then the function recalcFromResizer() is used to indicate for the input range to select how many ticks are displayed.

----------------------------------------------------------------------

## How to use the Chart and Inputs

In this section will explain the possible inputs in detail and give the examples of these inputs(listing 6.1). 

**Note that it is highly recommended to put the whole gem-gauge-chart into a div-element.**

```ts
<div class="example">
    <gem-gauge-chart
      [data]="data"
      [min]="-30"
      [max]="70"
      [unit]="Emission"
      [textValue]="Co2"
    ></gem-gauge-chart>
  </div>
```

_Listing 6.1 the Example for all the possiblele inputs_

### Data
Data in the form of _GemGaugeChartData_. 

Data is the most important part for charts. It provides the information for the charts. Without input data the chart will be empty. The _GemGaugeChartData_ consist of three parameters: _name_, _value_, _type_. More details could be found in the section [Data-Structure](#data-structure). 

### Min 
Define the minimum value and display on the chart. 

### Max
Define the maximum value and display on the chart.

### Unit
Define the text which is displayed under textValue.

### textValue
Define the text which describes the content of the data, and it will display in the middle of the chart.


----------------------------------------------------------------------

## References
[1] _The datapine Blog - Discover The Power Of Gauge Charts: Definition, Best Practices and Examples._ https://www.datapine.com/blog/gauge-chart-examples/. Accessed: 2022-08-12.

[2] _Datapine Documentation-Gauge Chart._ https://www.datapine.com/documentation/gauge-chart/. Accessed: 2022-08-12.

[3] _npm-linear-gauge-chart._ https://www.npmjs.com/package/linear-gauge-chart. Accessed: 2022-08-12.

[4] _ngx-gauge-chart._ https://swimlane.gitbook.io/ngx-charts/examples/gauge-chart. Accessed: 2022-08-12.