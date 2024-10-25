/* (c) https://github.com/MontiCore/monticore */
package example_charts;

//import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartData;
//import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartEntry;
//import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartEntryBuilder;
import domain.Person;
import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartData;
import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartDataBuilder;
import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartEntry;
import mc.fenix.charts.gemgaugecharttypes.GemGaugeChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GemGaugeChartExample extends GemGaugeChartExampleTOP {

  @Override
  public GemGaugeChartData getGemGaugeChart0Data() {
    List<GemGaugeChartEntry> entries = Arrays.asList(
        new GemGaugeChartEntryBuilder()
            .name("People born before 1993")
            .value(0)
            .type("#friends")
            .build().get(),
        new GemGaugeChartEntryBuilder()
            .name("People born between 1993 and 1998")
            .value(0)
            .type("#friends")
            .build().get(),
        new GemGaugeChartEntryBuilder()
            .name("People born after 1993")
            .value(0)
            .type("#friends")
            .build().get()
    );

    int numberFriendsGroup1 = 0;
    int numberPeopleInGroup1 = 0;
    int numberFriendsGroup2 = 0;
    int numberPeopleInGroup2 = 0;
    int numberFriendsGroup3 = 0;
    int numberPeopleInGroup3 = 0;
    for (Person person : this.getPersons()) {
      int birthday = Integer.valueOf(person.getBirthday().toString().substring(11, 15));
      if (birthday < 1993) {
        numberFriendsGroup1 += person.getFriendsList().size();
        numberPeopleInGroup1++;
      } else if (birthday >= 1993 && birthday <= 1998) {
        numberFriendsGroup2 += person.getFriendsList().size();
        numberPeopleInGroup2++;
      } else {
        numberFriendsGroup3 += person.getFriendsList().size();
        numberPeopleInGroup3++;
      }
    }

    entries.get(0).setValue((int) Math.round((double) numberFriendsGroup1 / numberPeopleInGroup1) * 10);
    entries.get(1).setValue((int) Math.round((double) numberFriendsGroup2 / numberPeopleInGroup2) * 10);
    entries.get(2).setValue((int) Math.round((double) numberFriendsGroup3 / numberPeopleInGroup3) * 10);

    GemGaugeChartData data = new GemGaugeChartDataBuilder().entries(entries).build().get();
    return data;
  }
}
