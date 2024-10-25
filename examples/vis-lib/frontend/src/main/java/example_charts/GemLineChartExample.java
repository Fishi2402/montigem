/* (c) https://github.com/MontiCore/monticore */
package example_charts;

import com.google.common.collect.Lists;
import domain.Person;
import domain.Stock;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartDataBuilder;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GemLineChartExample extends GemLineChartExampleTOP {

  @Override
  public GemLineChartData getLinechartData() {
    if (this.getPersons() == null) {
      return null;
    }

    List<GemLineChartEntry> entries = new ArrayList<>();
    List<String> labels = Lists.newArrayList("01/01/2022", "02/01/2022", "03/01/2022", "04/01/2022", "05/01/2022");

    Map<String, List<Person>> ageGroups = new HashMap<>();
    List<Person> before_1987 = new ArrayList<>();
    List<Person> between_1987_and_1991 = new ArrayList<>();
    List<Person> between_1991_and_1993 = new ArrayList<>();
    List<Person> between_1993_and_1998 = new ArrayList<>();
    List<Person> after_1998 = new ArrayList<>();

    for (Person person : this.getPersons()) {
      int birthday = person.getBirthday().getYear();
      if (birthday < 1987) {
        before_1987.add(person);
      }
      else if (birthday < 1991) {
        between_1987_and_1991.add(person);
      }
      else if (birthday < 1993) {
        between_1991_and_1993.add(person);
      }
      else if (birthday < 1998) {
        between_1993_and_1998.add(person);
      }
      else {
        after_1998.add(person);
      }
    }

    ageGroups.put("before 1987", before_1987);
    ageGroups.put("between 1987 and 1991", between_1987_and_1991);
    ageGroups.put("between 1991 and 1993", between_1991_and_1993);
    ageGroups.put("between 1993 and 1998", between_1993_and_1998);
    ageGroups.put("after 1998", after_1998);

    for (Map.Entry<String, List<Person>> entry : ageGroups.entrySet()) {
      int[] stocks = {0, 0, 0, 0, 0};
      for (Person person : entry.getValue()) {
        for (Stock stock : person.getStocksList()) {
          switch (stock.getDate().getDayOfMonth()) { // TODO GV
            case 1: {
              stocks[0] += stock.getPrice();
              break;
            }
            case 2: {
              stocks[1] += stock.getPrice();
              break;
            }
            case 3: {
              stocks[2] += stock.getPrice();
              break;
            }
            case 4: {
              stocks[3] += stock.getPrice();
              break;
            }
            case 5: {
              stocks[4] += stock.getPrice();
              break;
            }
          }
        }
      }
      String ageGroupname = entry.getKey();
      List<Integer> list = new ArrayList<>();
      for (int stock : stocks) {
        Integer i = stock;
        list.add(i);
      }
      Optional<GemLineChartEntry> chartEntry = new GemLineChartEntryBuilder().data(list).label("Stocks bought by people born " + ageGroupname).build();
      chartEntry.ifPresent(ce -> entries.add(ce));
    }

    GemLineChartData data = new GemLineChartDataBuilder()
        .entries(entries).labels(labels).build().get();
    return data;
  }

}
