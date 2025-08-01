# GemHeatmapChart

Heatmap-charts visualize different kinds of data, for instance temperature, in a tabular-like format. They are helpful for finding patterns, relationships between data or other kind of information.

## Usage example

`RowsTester` takes `GemHeatmapChart` in its body

```md
package example;
import mc.fenix.charts.GemHeatmapChart;
page RowsTester(
  GemHeatmapChartData data
) {
  @GemHeatmapChart(
    data = data
  );
}
```
<img src="../images/gemheatmapchart.png" alt="Heatmap Chart" width=40%/>


## GUI Model

`GemHeatmapChart.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemHeatmapChart.gui))

```
package mc.fenix.charts;

import mc.fenix.charts.GemHeatmapChartTypes.GemHeatmapChartData;

component GemHeatmapChart (
  GemHeatmapChartData data,
  String xLabel,
  String yLabel,
  Boolean timestampdata
)
```
| Name | Type | Description | Default value |
|---|---|---|---|
| data | GemHeatmapChartData | the complex data type applied to the chart and will be explained in following|`null`|
| xLabel | String |defines the label for the X-Axis|`null`|
| yLabel | String | defines the label for the Y-Axis|`null`|
| timestampdata | Boolean |decides wether the second relevant field for input parameter _data_ is "value" (if false) or "timestamp" (if true). The default is false|`false`|


### GemHeatmapChartTypes


`GemHeatmapChartTypes.cd` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemHeatmapChartTypes.cd))

```
package mc.fenix.charts;

import java.util.List;

classdiagram GemHeatmapChartTypes {
  class GemHeatmapChartData {
    List<GemHeatmapChartEntry> entries;
  }
  class GemHeatmapChartEntry {
    String name;
    Integer timestamp;
  }

}
```

-`GemHeatmapChartData` - consists of a parameter entries, an array of type GemHeatmapChartEntry
-`GemHeatmapChartEntry` - consist of two parameters, that are name of type string and timestamp of type number

### Data Model Example

```java
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartData;
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartEntry;
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;

@Component
public class GemHeatmapChartExample extends GemHeatmapChartExampleTOP {
  @Override
  public GemHeatmapChartData getHeatmapchartData() {
      
    List<GemHeatmapChartData> entries = new ArrayList<>();
        
    entries.add(new GemHeatmapChartEntryBuilder()
            .name("Test 2")
            .timestamp(1659001081090)
            .build().get());
    entries.add(new GemHeatmapChartEntryBuilder()
            .name("Test 1")
            .timestamp(1659001080790)
            .build().get());

    return new GemHeatmapChartData(entries);
  }
}
```
