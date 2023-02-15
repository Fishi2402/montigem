/* (c) https://github.com/MontiCore/monticore */
package stanley.personcarsedit;

import hardy.Car;
import hardy.Person;
import stanley.tmp.table.tabletypes.TableColumn;

import java.util.ArrayList;
import java.util.List;

// no implementation of error observer, since no component with ?Type parameters present
// i.e. no input fields exist on the page
public class PersonCarsEdit {

  protected final Person p;

  public PersonCarsEdit(Person p) {
    this.p = p;
  }

  // if the id of the component is specified:
  // method + id + parameter name
  // get + CarNum + Value
  // only getter, because value in @GemText(value = ...) is not ?String, but simply String
  public String getCarNumValue() {
    // the @GemText.value expects String, but we try to assign p.owns.size() which is int (Number)
    // Number -> String conversion is trivial, allow it
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
