package stanley;

// HWC-P

import hardy.Person;

public class EditierePersonGUI extends EditierePersonGUITOP{
  public EditierePersonGUI(Person p) {
    super(p);
  }

  @Override
  void decreaseAge(Person p) {
    p.setAge(p.getAge() - 1);
  }
}
