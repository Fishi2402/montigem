# GemVennDiagra

A Venn diagram is a diagram style that shows the logical relation between sets. It is used to teach elementary set theory, and to illustrate simple set relationships in probability, logic, statistics, linguistics and computer science.

## Usage example

`RowsTester` takes `GemVennDiagram` in its body

```md
package example;
import mc.fenix.charts.GemVennDiagram;
import mc.fenix.charts.GemVennDiagramTypes.GemVennDiagramData;
import mc.fenix.charts.GemVennDiagramTypes.GemVennDiagramEntry;
page RowsTester(
  GemVennDiagramData data,
  GemVennDiagramEntry entry
) {   
  @GemVennDiagram(
    data = data,
    entry = entry,
  );
}
```
<img src="../images/GemVennDiagram.png" alt="GemVennDiagram" width=40%/>

## GUI Model

`GemVennDiagram.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemVennDiagram.gui))

```
package mc.fenix.charts;

import mc.fenix.charts.GemVennDiagramTypes.GemVennDiagramData;

component GemVennDiagram(
    GemVennDiagramData data
)
```
| Name | Type | Description | Default value |
|---|---|---|---|
| data | GemVennDiagramData | is in form of GemVennDiagramData, it contains all information that will be used to plot the line chart|`null`|

### GemVennDiagramTypes

`GemVennDiagramTypes.cd` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/charts/GemVennDiagramTypes.cd))

```
package mc.fenix.charts;

import java.util.List;

classdiagram GemVennDiagramTypes {

  public class GemVennDiagramData {
    List<GemVennDiagramEntry> entries;
  }

  public class GemVennDiagramEntry {
    List<String> sets;
    String label;
    Integer size;
  }

}
```

`GemVennDiagramData` - is the data structure that contains `entries` which is a list of `GemVennDiagramEntry`

`GemVennDiagramEntry` - is the data structure that contains `sets`, `label` and `size`, `sets` is a list of string that represents the sets that the entry belongs to, `label` is the label of the entry, `size` is the size of the entry

### Data Model Example

```java
import mc.fenix.charts.gemvenndiagramtypes.GemVennDiagramData;
import mc.fenix.charts.gemvenndiagramtypes.GemVennDiagramEntry;
import mc.fenix.charts.gemvenndiagramtypes.GemVennDiagramEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class GemVennDiagramExample extends GemVennDiagramExampleTOP {
  @Override
  public GemVennDiagramData getVenndiagrammData() {

    List<GemVennDiagramEntry> entries = new ArrayList<>();

    entries.add(new GemVennDiagramEntryBuilder()
            .label(Optional.of("Test 1"))
            .sets(Lists.newArrayList("test1"))
            .size(6)
            .build().get());

    entries.add(new GemVennDiagramEntryBuilder()
            .label(Optional.of("Test 2"))
            .sets(Lists.newArrayList("test2"))
            .size(3)
            .build().get());

    entries.add(new GemVennDiagramEntryBuilder()
            .label(Optional.of("Test 3"))
            .sets(Lists.newArrayList("test3"))
            .size(2)
            .build().get());

    entries.add(new GemVennDiagramEntryBuilder()
            .sets(Lists.newArrayList("test1", "test2"))
            .size(1)
            .build().get());

    return new GemBarChartData(entries);
  }
}
```
