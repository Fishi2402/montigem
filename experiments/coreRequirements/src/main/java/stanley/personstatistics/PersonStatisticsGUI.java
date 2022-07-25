package stanley.personstatistics;

import hardy.Person;

import umlp.rte.gui.PieData;

import java.util.List;
import java.util.ArrayList;

public class PersonStatisticsGUI extends PersonStatisticsGUITOP {

  public PersonStatisticsGUI(List<Person> ps) {
    super(ps);
  }

  public void setPieData(List<Person> ps) {
    List<PieData> result = new ArrayList<>();
    // Pie data with description. Description is a unique identifiert for a pie slice
    // initial value for both is 0
    PieData slice1 = new PieData("younger than 20", 0);
    result.add(slice1);

    PieData slice2 = new PieData("older than 20 or 20", 0);
    result.add(slice2);
    // count persons younger than 20 and rest
    for (Person p : ps) {
      if (p.getAge() < 20) {
        slice1.setValue(slice1.getValue() + 1);
      } else {
        slice2.setValue(slice2.getValue() + 1);
      }
    }
  }

}
