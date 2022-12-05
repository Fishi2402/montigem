package stanley.personstatistics;

// HWC-P

import hardy.Person;

// PieData is a class generated from a class diagram modeled for Pie component
/* Consider:
 * PieData {
 *   String description; // description of a group of entries conforming to some condition
 *   int value; // number of entries conforming to some condition
 * }
 */
import umlp.rte.gui.PieData;

import java.util.List;
import java.util.ArrayList;

public abstract class PersonStatisticsTOP {

  protected List<PieData> pieData = new ArrayList();

  public PersonStatisticsTOP(List<Person> ps) {
    setPieData(ps);
  }

  public abstract void setPieData(List<Person> pieData);

  public List<PieData> getPieData() {
    return pieData;
  }

  public List<PieData> getData(List<Person> ps) {
    return getPieData();
  }

}
