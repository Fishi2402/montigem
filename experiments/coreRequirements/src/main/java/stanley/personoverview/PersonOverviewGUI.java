package stanley.personoverview;

import hardy.Person;
import umlp.rte.GemList;
// Col is a frontend (client) RTE class representing table column configuration
import umlp.rte.Col;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class PersonOverviewGUI {
  protected final List<Person> p;

  // configuration of table columns
  protected List<Col> tableColumns = new ArrayList<>();

  public PersonOverviewGUI(List<Person> ps) {
    this.ps = ps;

    PersonErrorCollector.addErrorObserver(this);

    setRows(ps);

    List<Col> initialTableColumns = new ArrayList();
    initialTableColumns.add(new Col("age"));
    initialTableColumns.add(new Col("nickname"));
    setTableColumns(initialTableColumns);
  }

  public List<Object> getRows() {
    return this.ps;
  }

  public void setRows(List<Object> rows) {
    this.rows = rows;
  }

  public List<Col> getTableColumns() {
    return tableColumns;
  }

  public void setTableColumns(List<Col> value) {
    tableColumns = value;
  }

}
