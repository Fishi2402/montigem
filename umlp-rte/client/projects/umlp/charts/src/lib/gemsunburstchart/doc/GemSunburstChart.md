# Gem-Sunburst-Chart generating

----------------------------------------------------------------------
## Not the full sunburst doc!

Note that this documentation of the sunburst chart only focuses on the generating part. [Click here to see the main **sunburst chart documentation**](SunburstChart.md)



----------------------------------------------------------------------


## Overview

- [GUI Model and Class Diagram](#gui-model-and-class-diagram)
- [Implementation](#implementation)

----------------------------------------------------------------------

## GUI Model and Class Diagram

In this section, GUI model and class diagram implementation details are described and examples are given.

`GemSunburstChart.gui`([open](../GemSunburstChart.gui))
```
package charts.src.lib.gemsunburstchart;

import charts.src.lib.gemsunburstchart.GemSunburstChartTypes.GemSunburstData;
import charts.src.lib.gemsunburstchart.GemSunburstChartTypes.GemSDNshort;

component GemSunburstChart (
  GemSunburstData data,
  GemSDNshort dataShort,
  List<String> colors, 
  Boolean ignoreError
)
```
_Listing 4.1: GUI model of sunburst-chart._

Listing 4.1 shows an example GUI model for sunburst-charts with input parameters ***data***, ***dataShort***, ***colors*** and ***ignoreError***.

a Java-like package with the corresponding name and path is stated before the component is declared. 

The input parameters and what they mean are described in section of the main documentation [How to use the Chart and Inputs](SunburstChart.md#how-to-use-the-chart-and-inputs).

In addition to a GUI model, a _class diagram_ is needed to describe additional types of more complex components, such as charts. Again, a Java-like import is done in this case. An example of a class diagram model can be seen in Listing 4.2.

`GemSunburstChartTypes.cd` ([open](../GemSunburstChartTypes.cd)
```
package charts.src.lib.gemsunburstchart;

classdiagram GemSunburstChartTypes {
  class GemSunburstData {
    GemSunburstEntry entry;
    List<GemSunburstData> subEntries;
  }

  class GemSunburstEntry {
    String label;
    Integer value;
  }

  class GemSDNshort {
    List c;
    List<GemSDNshort> sN;
  }
  
}
```
_Listing 4.2: Class diagram of sunburst-chart GUI model._

An additional file with interfaces is generated  from the class diagram, which can be imported and used in other components, as seen in Listing 4.1. The generated file  _gem-sunburst-chart-types.types-gen.ts_ can be seen in Listing 4.3.

```ts
  export interface GemSunburstData {
       entry?: GemSunburstEntry;
       subEntries?: GemSunburstData[];
  }
  export interface GemSunburstEntry {
       label?: string;
       value?: number;
  }
  export interface GemSDNshort {
       c?: any[];
       sN?: GemSDNshort[];
  }
```
_Listing 4.3: Generated interfaces of sunburst-chart._


----------------------------------------------------------------------


## Implementation

In this section, implementation details and how hand-written files are handled are described.


### Hand-written Code Extension

To make use of our GUI model, the hand-written code  in _gem-sunburst-chart.component.ts_ has to be extended with the generated code. To achieve this, the generated file _gem-sunburst-chart.component-gen.ts_ must be imported into the hand-written _gem-sunburst-chart.component.ts_ and the generated class _GemSunburstChart_ is used as a parent class for the hand-written one, as seen in the following Listing.

`gem-sunburst-chart.component.ts` ([open](../gem-sunburst-chart.component.ts))
```ts
// ... other imports
import { GemSunburstChart as GemSunburstChartTOP } from "./gem-sunburst-chart/gem-sunburst-chart.component-gen";
import { GemSunburstData, GemSunburstEntry, GemSDNshort } from "./gem-sunburst-chart-types/gem-sunburst-chart-types.types-gen";

@Component({
  // ... config
})

export class GemSunburstChartComponent implements GemSunburstChartTOP {
  // ... implementation
}
```

_Listing 5.1: Extending generated code with hand-written one._

### Exporting GemSunburstChart

To be able to use the sunburst-chart component, the classes of the component need to be publicly available. To achieve this, a file named _index.ts_ is created inside the _gemsunburstchart_ folder and the generated files are exported.

`index.ts`([open](../index.ts))
```ts
export * from "./gem-sunburst-chart.types";
export * from "./gemsunburstchart.module-gen";
export * from "./gem-sunburst-chart.component";
export * from './gem-sunburst-chart-types/gem-sunburst-chart-types.types-gen';
```
_Listing 5.2: index.ts file of gemsunburstchart folder._

Moreover, the _index.ts_ file in the parent folder needs to be adjusted as well. 

`index.ts`([open](../../../index.ts))
```ts
// other exports ...
export * from "./lib/gemsunburstchart";
```
_Listing 5.3: index.ts file of the parent folder._