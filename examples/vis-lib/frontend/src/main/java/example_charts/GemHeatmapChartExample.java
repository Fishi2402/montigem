/* (c) https://github.com/MontiCore/monticore */
package example_charts;

import domain.Person;
import domain.Stock;
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartData;
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartEntry;
import mc.fenix.charts.gemheatmapcharttypes.GemHeatmapChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GemHeatmapChartExample extends GemHeatmapChartExampleTOP {

  @Override
  public GemHeatmapChartData getGemHeatmapChart0Data() {
    if (this.getPersons() == null) {
      return null;
    }

    List<GemHeatmapChartEntry> entries = new ArrayList<>();
    entries.add(new GemHeatmapChartEntryBuilder().name("Older than 32").timestamp(0).build().get());
    entries.add(new GemHeatmapChartEntryBuilder().name("Between 30 and 32").timestamp(0).build().get());
    entries.add(new GemHeatmapChartEntryBuilder().name("Between 28 and 29").timestamp(0).build().get());
    entries.add(new GemHeatmapChartEntryBuilder().name("Between 27 and 23").timestamp(0).build().get());
    entries.add(new GemHeatmapChartEntryBuilder().name("Younger than 23").timestamp(0).build().get());

    for (Person person : this.getPersons()) {
      if (person.getStocksList().isEmpty()) {
        continue;
      }
      int birthday = person.getBirthday().getYear();
      int sumStocks = 0;
      for (Stock stock : person.getStocksList()) {
        sumStocks += stock.getPrice();
      }
      if (birthday < 1992) {
        entries.get(0).setTimestamp(entries.get(0).getTimestamp() + sumStocks);
      }
      else if (birthday < 1995) {
        entries.get(1).setTimestamp(entries.get(1).getTimestamp() + sumStocks);
      }
      else if (birthday < 1997) {
        entries.get(2).setTimestamp(entries.get(2).getTimestamp() + sumStocks);
      }
      else if (birthday < 2002) {
        entries.get(3).setTimestamp(entries.get(3).getTimestamp() + sumStocks);
      }
      else {
        entries.get(4).setTimestamp(entries.get(4).getTimestamp() + sumStocks);
      }
    }
    return new GemHeatmapChartData(entries);
  }

}
