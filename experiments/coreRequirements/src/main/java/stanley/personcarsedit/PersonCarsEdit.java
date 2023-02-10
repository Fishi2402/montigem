/* (c) https://github.com/MontiCore/monticore */
package stanley.personcarsedit;

import hardy.Car;
import hardy.Person;
import stanley.guisim.tabletypes.TableColumn;

import java.util.ArrayList;
import java.util.List;

public class PersonCarsEdit {

  protected final Person p;

  public PersonCarsEdit(Person p) {
    this.p = p;
  }

  // only getter, because value in @Text(value = ...) is not ?String, but simply String
  public String getCarNumValue() {
    // recognize, that size returns int and add String.valueOf to return String
    return String.valueOf(p.getOwns().size());
  }

  public List<Car> getTRows() {
    // Type of rows is underspecified List, infer from the usage in model
    return p.getOwns();
  }

  public List<TableColumn> getTColumns() {
    List<TableColumn> res = new ArrayList();
    return res;
  }

  public void navLeftClick() {
    this.navigate();
  }

  protected void navigate() {}

}
