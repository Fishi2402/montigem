package stanley;

// HWC-T
// HWC-P: Will be converted to frontend code

import hardy.FlensburgManager;
import hardy.Person;

public class PersonPageGUI extends PersonPageGUITOP {
  public PersonPageGUI(Person p) {
    super(p);
  }

  @Override
  public void decreaseAge() {
    p.setAge(p.getAge() - 1);
    FlensburgManager.updatePerson(p);
  }
}
