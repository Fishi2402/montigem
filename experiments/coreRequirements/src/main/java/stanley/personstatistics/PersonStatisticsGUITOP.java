package stanley.personstatistics;

// HWC-P

import hardy.PersonBuilder;

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

public class PersonStatisticsGUITOP {

  List<PieData> pieData = new ArrayList();

  public PersonStatisticsGUITOP(List<Person> ps) {
    setPieData(ps);
  }

  public void setPieData(List<PieData> pieData) {
    this.pieData = pieData;
  }

  public List<PieData> getPieData() {
    return pieData;
  }

  public List<PieData> getData(List<Person> ps) {
    return getPieData();
  }

}
