# GemBarChart

A Bar-Chart present a set of two dimensional data with a number of rectangular bars that can go _horizontally_ or _vertically_, with their values being proportional to what they represent.
The chart itself visualizes categories (given as Strings) on one axis and a discrete integer value on the other as height or size. Additionally, the bars can be _stacked_ or _aligned_ to visualizes multiple sub-categories of a second data dimension.

## Usage example

`RowsTester` takes `GemBarChart` in its body.

```md
package example;
import mc.fenix.charts.GemBarChart;
page RowsTester(
  GemBarChartData data, 
  Integer maxValue, 
  Integer minValue, 
  Object displayValue
) {
  @GemBarChart(
    data = data,
    stacked = true,
    maxValue = maxValue,
    minValue = minValue,
    displayValue = displayValue
  ); 
}
```
<img src="../images/gembarchart_green_upwards.png" alt="Bar Chart" width=40%/>

## GUI Model

`GemBarChart.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemBarChart.gui))

```
package mc.fenix.charts;

import mc.fenix.charts.GemBarChartTypes.GemBarChartData;

component GemBarChart(
  GemBarChartData data,
  Object displayValue,
  Boolean stacked,
  Boolean overlapping,
  Integer maxValue,
  Integer minValue,
  boolean showConfig = true
)
```
| Name | Type | Description | Default value |
|---|---|---|---|
| data | GemBarChartData  | the data to be displayed | `null` (no text is set) |
| displayValue | Object  | the value to be displayed (e.g., on hover or label) | `null` |
| stacked | Boolean  | the chart is stacked or not | `false` (not stacked) |
| overlapping | Boolean | whether bars can overlap visually (for comparison purposes) | `false` (not overlapping) |
| maxValue | Integer  | is the maximum value of the chart | `null` (no element is set) |
| minValue | Integer  | is the minimum value of the chart | `null` (no element is set) |
| showConfig | boolean | whether the chart's configuration panel is shown | `true` |

### GemBarChartTypes

`GemBarChartTypes` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemBarChartTypes.cd))

```
package mc.fenix.charts;

import java.util.List;

classdiagram GemBarChartTypes {
  public class GemBarChartData {
    List<GemBarChartEntry> entries;
    List<String> labels;
  }
  public class GemBarChartEntry {
    String label;
    String stack;
    List<Integer> data;
    String backgroundColor;
    String hoverBackgroundColor;
  }
}
```
- `GemBarChartData` is the data structure that contains `entries`, which is used to plot the bar chart
 and `labels` which is used to label the x-axis of the bar chart

- `GemBarChartEntry` is the data structure that contains `label`, `stack`, `data`, `backgroundColor` and `hoverBackgroundColor` that will be used to plot the bar chart

### Data Model Example
```java
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntry;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class GemBarChartExample extends GemBarChartExampleTOP {
 @Override
 public GemBarChartData getBarchartData() {

  List<String> labels = Arrays.asList("Cherry St.", "Strawberry Mall", "Peach St.", "Apple Rd.");
  
  List<GemBarChartEntry> entries = new ArrayList<>();

  entries.add(new GemBarChartEntryBuilder()
          .label(Optional.of("Clothing"))
          .data(Arrays.asList(8000, 7500, 4500, 4000))
          .build().get());

  entries.add(new GemBarChartEntryBuilder()
          .label(Optional.of("Foods"))
          .data(Arrays.asList(5000, 3500, 4000, 3000))
          .build().get());

  entries.add(new GemBarChartEntryBuilder()
          .label(Optional.of("Liquids"))
          .data(Arrays.asList(1000, 900, 1000, 5000))
          .build().get());

  return new GemBarChartData(entries, labels);
 }
}
```
