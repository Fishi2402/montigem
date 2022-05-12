package stanley.editiereperson;

// HWC-P

import hardy.Person;

public class EditierePersonGUI extends EditierePersonGUITOP {
  public EditierePersonGUI(Person p) {
    super(p);
  }

  @Override
  protected void decreaseAge(Person p) {
    // TODO AHe: Error handling? Permission etc
    p.setAge(p.getAge() - 1);
  }
}
