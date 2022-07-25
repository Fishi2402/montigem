package stanley.personstatistics;

import hardy.Person;

// PieData is a class generated from a class diagram modeled for Pie component
/* Consider:
 * PieData {
 *   String description; // description of a group of entries conforming to some condition
 *   int value; // number of entries conforming to some condition
 * }
 */
import umlp.rte.PieData;

import java.util.List;
import java.util.ArrayList;

public class PersonStatisticsGUI extends PersonStatisticsGUITOP {

  public PersonStatisticsGUI(List<Person> ps) {
    super(ps);
  }

  public void setPieData(List<PieData> pieData) {
    List<PieData> pieData = new ArrayList<>();
    // Pie data with description. Description is a unique identifiert for a pie slice
    // initial value for both is 0
    PieData slice1 = new PieData("younger than 20", 0);
    pieData.add(slice1);

    PieData slice2 = new PieData("older than 20 or 20", 0);
    pieData.add(slice2);
    // count persons younger than 20 and rest
    for (Person p : ps) {
      if (p.age < 20) {
        slice1.value++;
      } else {
        slice2.value++;
      }
    }
    return pieData;
  }

}
