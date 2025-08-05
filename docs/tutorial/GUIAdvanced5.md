---
hide:
  - navigation
---

# Charts from the Backend

Lastly sometimes we do not want to load all data from the Backend into the FrontEnd
to then only use it for calculating and displaying the chart.
In this case we can add a service task as introduced in [ClassDiagramAdvanced](./ClassDiagramAdvanced.md).

In the following example we implement the same functionality as before but this time 
in the Backend instead on the ClientSide. The advantage is that is much faster, but 
it comes with a disadvantage of not necessarily being automatically updated.

You have to correctly import the `GemLineChartData`. The import for chart types defined 
in the FrontEnd-RTE follow the schema `mc.fenix.charts.${ChartName}Types.${ChartData}`   

```java
import mc.fenix.charts.GemLineChartTypes.GemLineChartData;

umlp Carrental {

  <<singleton>>
  class App {
    GemLineChartData getMileageDataIncrease(List<Car> cars);
  }

}
```

We can also include this service directly in our GUI-Page like this:

```typescript
page Dashboard(List<Car> cars, App app) {
  @NavContainer(content = [
    @GemRow(wrap = "wrap", rowGap = "10px", hAlign = "space-evenly", components = [

      @GemCard(
        width = "40%",
        title = "Mileage increase over time",
        component = @GemLineChart(data = app.getMileageDataIncrease(cars))
      )

    ])
  ]);
}
```

We still have to implement the service in the Java Backend e.g. like this.
Important to note: The `@BackendOnly` annotation tells JSweet to not transpile
the method body. This is oftentimes helpful, when we want to avoid TypeScript  
that would not compile because it is not correctly transpiled and this code 
is only executed in the Java Backend. The method header is still transpiled, though. 
If the transpilation of the method leads to problems, there is also the possibility 
to use the `@Erased` annotation that tells JSweet to ignore the method. 

```java
package carrental.commands;

import carrental.*;

import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartDataBuilder;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntryBuilder;

import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;
import umlp.backendrte.command.SimpleResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GetMileageDataIncrease extends GetMileageDataIncreaseTOP {

  public GetMileageDataIncrease(List<Long> cars) {
    super(cars);
  }

  @Override
  public SimpleResult<GemLineChartData> doAction() {
    Optional<GemLineChartData> res = calculateMileageData(this.getCarsGemIdList());
      if(res.isEmpty()) {
        return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, GemLineChartData.class.getName());
      }
      return new SimpleResult<GemLineChartData>(getId(), res.get(), GemLineChartData.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<GemLineChartData> calculateMileageData(List<Long> carIds) {

    List<Car> cars = carIds.stream().map(cId -> CarrentalManager.carOpt(cId).get()).collect(Collectors.toList());

    if(false) {
      return Optional.empty();
    }

    // create an array of dates to use them as x axis points later
    List<LocalDateTime> dates = new ArrayList<>();

    for (Car c : cars) {
      for (MileageDataPoint mileage : c.getMileageDataPointList()) {
        dates.add(mileage.getRecordDate());
      }
    }

    // sort dates
    dates.sort((l,r) -> l.compareTo(r));

    List<String> labels = new ArrayList<>();
    for (LocalDateTime date : dates){
      String xAxisPoint = date.toString();
      if (!labels.contains(xAxisPoint)) {
        // use string representation of (non-repeating) dates as x axis points
        labels.add(xAxisPoint);
      }
    }

    List<GemLineChartEntry> entries = new ArrayList<>();
    for (Car c : cars){
      // create an entry for each car:
      // label (1st arg) is the name of the car, data (2nd arg) are the mileage data for each date
      List<Integer> valuesForLineChart = new ArrayList<>(Collections.nCopies(labels.size(), null));

      GemLineChartEntry entry = new GemLineChartEntryBuilder().label(
        c.getMake()+" "+c.getModel()+" "+c.getName()).data(valuesForLineChart).build().get();
      entries.add(entry);
      for (MileageDataPoint mileage : c.getMileageDataPointList()){
        // find what position the mileage data needs to have to represent specific date
        String xAxisPoint = mileage.getRecordDate().toString();
        int xAxisPointIndex = labels.indexOf(xAxisPoint);
        entry.getDataList().set(xAxisPointIndex, (int) mileage.getMileage().floatValue());
      }
    }

    // optional: add interpolated itermediate points to entries where the data is not available
    for (GemLineChartEntry entry : entries){
      for (int i = 0; i < entry.getDataList().size(); ++i) {
        Integer data = entry.getData(i);
        if (data == null) {
          this.interpolateData(entry.getDataList(), i);
        }
      }
    }

    GemLineChartData data =
            new GemLineChartDataBuilder().entries(entries).labels(labels).build().get();
    return Optional.of(data);

  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public void interpolateData(List<Integer> stat, int interpolatedIndex) {
    int left = 0;
    int leftI = 0;
    int right = 0;
    int rightI = 0;
    // find non-null point left of interpolated one
    for (int i = 0; i < stat.size(); ++i) {
      if (stat.get(i) != null && interpolatedIndex > i) {
        left = stat.get(i);
        leftI = i;
      }
    }

    // find non-null point right of interpolated one
    for (int i = 0; i < stat.size(); ++i) {
      if (stat.get(i) != null && interpolatedIndex < i) {
        right = stat.get(i);
        rightI = i;
        break;
      }
    }

    stat.set(interpolatedIndex,left + ((right - left) / (rightI - leftI)) * (interpolatedIndex - leftI));
  }
}
```

[Previous page](./GUIAdvanced4.md)
