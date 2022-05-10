package stanley;

// HWC-P

import hardy.Person;

public class EditierePersonGUI extends EditierePersonGUITOP{
  public EditierePersonGUI(Person p) {
    super(p);
  }

  @Override
  void decreaseAge(Person p) {
    // TODO AHe: Error handling? Permission etc
    p.setAge(p.getAge() - 1);
  }
}
