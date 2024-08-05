/* (c) https://github.com/MontiCore/monticore */
package example_charts;

import def.js.Array;
import def.js.Array;
import domain.Person;
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntry;
import org.checkerframework.checker.units.qual.A;
import umlp.guiutil.UMLPGuiArray;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class GemBarChartExample extends GemBarChartExampleTOP {
  @Override
  public GemBarChartData getBarchartData() {
    List<String> labels = Arrays.asList("People born before 1993", "People born between 1993 and 1998", "People born after 1998");

    List<GemBarChartEntry> dataEntries = new ArrayList<>();
    dataEntries.add(new GemBarChartEntry(Optional.of("People with less than 2 friends"), Optional.empty(),
        Arrays.asList(0, 0, 0), Optional.empty(), Optional.empty()));
    dataEntries.add(new GemBarChartEntry(Optional.of("People with more than 2 friends"), Optional.empty(),
        Arrays.asList(0, 0, 0), Optional.empty(), Optional.empty()));

    for (Person person : this.getPersons()) {
      int birthday = person.getBirthday().getYear();
      int numberFriends = person.getFriendsList().size();
      if (birthday < 1993) {
        if (numberFriends <= 2) {
          incrementFirstDataValue(dataEntries, 0, 0);
        } else {
          incrementFirstDataValue(dataEntries, 1, 0);
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        if (numberFriends <= 2) {
          incrementFirstDataValue(dataEntries, 0, 1);
        } else {
          incrementFirstDataValue(dataEntries, 1, 1);
        }
      } else {
        if (numberFriends <= 2) {
          incrementFirstDataValue(dataEntries, 0, 2);
        } else {
          incrementFirstDataValue(dataEntries, 1, 2);
        }
      }
    }

    return new GemBarChartData(new UMLPGuiArray<>(dataEntries), labels);
  }

  private static void incrementFirstDataValue(List<GemBarChartEntry> dataEntries, int index1, int index2) {

    ((UMLPGuiArray<Integer>) dataEntries.get(index1).getDataList()).set(index2, ((UMLPGuiArray<Integer>) dataEntries.get(index1).getDataList()).get(index2) + 1);
  }
}
