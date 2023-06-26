# PieChart

----------------------------------------------------------------------

## Goal

The goal of this project is to implement and test a new Angular library which contains a chart, in this case a pie-chart. The chart should be customizable using different inputs and data structure. The functions used in the component were tested with unit-tests.

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Overview](#overview)
- [Pie-Charts](#pie-charts)
- [Data-Structure](#data-structure)
- [GUI Model, class diagram and hand-written extension](#gui-model-class-diagram-and-hand-written-extension)
- [Implementation](#implementation)
- [How to use the Chart and Inputs](#how-to-use-the-chart-and-inputs)
- [References](#references) 

----------------------------------------------------------------------

## Pie-Charts

Pie chart is also called circle chart. It looks like a circle, which is divided to slices to visualize information. Different categories of data could be indicated by different colors. It is helpful to observe the numerical proportion of data.  An example of pie chart can be seen in following figure and detailed information can be found in [1].

<img src="https://datavizcatalogue.com/methods/images/top_images/pie_chart.png" alt="PieCharts" width=80%>

_An example of pie charts_

----------------------------------------------------------------------

## Data Structure

The data structure is defined in _gem-pie-chart-types.types-gen.ts_. The used data structure is _GemPieChartData_ and _GemPieChartTotal_.

### GemPieChartData

GemPieChartData consists of a parameter _entries_, an array of type _GemPieChartEntry_. _GemPieChartEntry_ elements consist of three parameters, they are: integer value, string label, and string color.

An example of _GemPieChartData_ can be found in the following listing 4.1:

```ts
const entries: GemPieChartEntry[] = [
  {
    value: 100,
    label: "Freizeit",
    
  },
  {
    value: 200,
    label: "Essen",
  },
  {
    value: 350,
    label: "Miete",
    color: "red",
  },
];

const data: GemPieChartData = {
  entries,
};

```
_Listing 4.1: Example of GemPieChartData_

### GemPieChartTotal

GemPieChartTotal consists of three parameters, they are: integer limit, string text and string title.



An example of _GemPieChartTotal_  can be found in listing 4.2:

```ts
const total: GemPieChartTotal = {
  limit: 500,
  text: 'Die Ausgaben übersteigen das Budget für den Monat',
  title: 'monatlicher Gehalt',
};
```

_listing 4.2: Example of GemPieChartTotal_

----------------------------------------------------------------------

## GUI Model, class diagram and hand-written extension

In this section, the first part is to create the basic GUI model, which is the typical component declaration. The second part is to add an additional class diagram model, which describes the data type of gauge chart. The third part is to extend the generated code with a couple of hand-written code.

### Basic Models

The GUI Model is the component declaration. 

1. First create a new folder in charts library with name _gempiechart_. 
2. Then create the .gui file with name _GemPieChart.gui_. And add the following code: 

```
package charts.src.lib.gempiechart;

import charts.src.lib.gempiechart.GemPieChartTypes.GemPieChartData;
import charts.src.lib.gempiechart.GemPieChartTypes.GemPieChartTotal;

component GemPieChart(
    GemPieChartData data,
    GemPieChartTotal total,
    Integer innerRadius,
    Boolean displayLegend
)

```
In this file, we state the Java-like package name _gempiechart_, import the additional data type _GemPieChartData_ and _GemPieChartTotal_, declare the component named _GemPieChart_, and define input parameters _data, total;, innerRadius_ and _displayLegend_. 

### Class Diagram

For the complex components such as charts we need to create an additional class diagram to declare the additional types.
1. Create the .cd file with name _GemPieChartTypes.cd_. According to description in section [Data-Structure](#data-structure) we add the following code:

```
package charts.src.lib.gempiechart;

classdiagram GemPieChartTypes {
    
  class GemPieChartData {
    List<GemPieChartEntry> entries;
  }

  class GemPieChartEntry {
    Integer value;
    String label;
    String backgroundColor;
  }

    class GemPieChartTotal {
      Integer limit;
      String text;
      String title;
    }
}
```
The additional interfaces could be generated, which can be imported and used in any component.

### Hand-written Code Extension

1. Create a hand-written .html file with name _gem-pie-chart.component.html_. We use this hand-written one instead of the generated one. This file will be automatically referenced by the generated TypeScript file.

2. Create a hand-written .scss file with name _gem-pie-chart.component.scss_.
This file will instead of the generated one and will be automatically referenced by the generated TypeScript file.

3. Create a hand-written .ts file with name _gem-pie-chart.component.ts_. The generated TS file can be imported in this hand-written one, and we use the generated class as a parent class for this hand-written one. Besides we need to define the configuration in Component decorator such as _templatedUrl, styleUrls_.
```ts
import { GemPieChartData } from './gem-pie-chart-types/gem-pie-chart-types.types-gen';
import { GemPieChart as GemPieChartTOP } from './gem-pie-chart/gem-pie-chart.component-gen';


@Component({
  selector: 'gem-pie-chart',
  templateUrl: './gem-pie-chart.component.html',
  styleUrls: ['./gem-pie-chart.component.scss']
})

export class GemPieChart extends GemPieChartTOP {
    //... implementations
}

```
4. Create a hand-written module file with name _gempiechart.module.ts_, so that we could add extra imports to the generated model. The import list in the hand-written one will be added in the generated file. 
For example, in this case we need to add an additional model _GemPieChartLegendModule_ in order to configure the legend for the pie chart. Corresponding files could be found in folder _gem-pie-chart-legend_. Then we add it into the import list.

```
import { ChartsModule } from "ng2-charts";
import { GemPieChartLegendModule } from "./gem-pie-chart-legend/gem-pie-chart-legend.module";

export const imports = [
  GemPieChartLegendModule,
  ChartsModule
]

```

### Export the classes

1. Create index.ts in folder _gempiechart_, with the following content:
```
export * from "./gempiechart.module-gen";
export * from "./gem-pie-chart.component";
export * from "./gem-pie-chart-types/gem-pie-chart-types.types-gen";

``` 
in order to export the classes, make them publicly available.

2. Export this index.ts file from the parent index.ts file: 
```
export * from "./lib/gempiechart";
```

----------------------------------------------------------------------

## Implementation

In this section the implementation details will be discussed.

### colorsWithoutInput
This function is used to set the color of entries where color is not set in input.

### balancePercentage
This function use to balance entries, so that their sum could equal to 100 percent. There are two parameters, arr and limit. Array arr contains the values of all entries, and limit is the sum of values of the entries. This function will return an array with the values of entries but changed them in percent.

### updateTotal
This function is use to check to see if the limit has been exceeded by bringing in new values for the entries or new entries.

### updateOptions
This is used to save all the unchanged setting of the chart when some specific setting is changed.



----------------------------------------------------------------------

## How to use the Chart and Inputs

In this section will explain the possible inputs in detail and give the examples of these inputs(listing 6.1). 

**Note that it is highly recommended to put the whole gem-pie-chart into a div-element.**

```ts
<div class="example">
  <gem-pie-chart
    [data]="data"
    [total]="total"
    [innerRadius]="innerRadius"
    [displayLegend]="displayLegend"
  >
  </gem-pie-chart>
</div>
```

_Listing 6.1 the Example for all the possbile inputs_

### Data
Data in the form of _GemPieChartData_. 

Data is the most important part for charts. It provides the information for the charts.The _GemPieChartData_ consist of entries, and the _GemPieChartEntry_ consist of three parameters: _value_, _label_, _color_. 

 More details could be found in the section [Data-Structure](#data-structure). 

### total 
Total in the form of _GemPieChartTotal_.
_GemPieChartTotal_ consists of three parameters: _limit_, _text_, _title_.
More details could be found in the section [Data-Structure](#data-structure). 

### innerRadius
Define the size of the inner radius.

### displayLegend
Set to display the legend or not.


----------------------------------------------------------------------

## References
[1] _The Data Visualisation Catalogue._ https://datavizcatalogue.com/methods/pie_chart.html Accessed: 2022-08-12.
