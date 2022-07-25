package stanley.editierepersonchildren;

import hardy.Person;
import hardy.PersonErrorCollector;
import hardy.PersonErrorObserver;

import java.util.List;
import java.util.ArrayList;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class EditierePersonChildrenGUI implements PersonErrorObserver {
  protected final Person p;

  protected String numberInputError = "";

  protected List<String> numberInputErrorChildren = new ArrayList();

  public EditierePersonChildrenGUI(Person p) {
    this.p = p;

    PersonErrorCollector.addErrorObserver(this);
  }

  public Integer getNumberInput() { return p.getAge(); }

  public String getNumberInputError() { return numberInputError; }

  public List<Integer> getNumberInputChildren() {
    List<Integer> result = new ArrayList();
    for (Person child : p.getChildren()) {
      child.getAge();
    }
    return result;
  }

  public List<String> getNumberInputError2() {
    return numberInputErrorChildren;
  }

}
