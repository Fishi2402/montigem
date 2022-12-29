/* (c) https://github.com/MontiCore/monticore */
package stanley.personoverview;

import hardy.Person;
// Col is a frontend (client) RTE class representing table column configuration
import umlp.rte.gui.Col;

import java.util.List;
import java.util.ArrayList;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class PersonOverview {
  protected List<Person> ps;

  protected List<? extends Object> rows = new ArrayList<>();

  // configuration of table columns
  protected List<Col> tableColumns = new ArrayList<>();

  public PersonOverview(List<Person> ps) {
    this.ps = ps;

    setRows(ps);

    List<Col> initialTableColumns = new ArrayList();
    initialTableColumns.add(new Col("age"));
    initialTableColumns.add(new Col("nickname"));
    setTableColumns(initialTableColumns);
  }

  public List<? extends Object> getRows() {
    return this.ps;
  }

  public void setRows(List<? extends Object> rows) {
    this.rows = rows;
  }

  public List<Col> getTableColumns() {
    return tableColumns;
  }

  public void setTableColumns(List<Col> value) {
    tableColumns = value;
  }

}
