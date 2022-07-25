package stanley.editierepersonchildren;

import hardy.Person;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class EditierePersonChildrenGUI {
  protected final Person p;

  protected String numberInputError1 = "";
  protected String numberInputError2 = "";

  public EditierePersonChildrenGUI(Person p) {
    this.p = p;

    PersonErrorCollector.addErrorObserver(this);
  }

  public Integer getNumberInput1() { return p.getAge(); }
  public String getNumberInputError1() { return numberInputError1; }

  public Integer getNumberInput2() { return p.child.getAge(); }
  public String getNumberInputError2() { return numberInputError2; }

}
