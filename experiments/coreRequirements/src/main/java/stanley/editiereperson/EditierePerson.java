package stanley.editiereperson;

// HWC-P

import hardy.Person;

public class EditierePerson extends EditierePersonTOP {
  public EditierePerson(Person p) {
    super(p);
  }

  @Override
  protected void decreaseAge(Person p) {
    // TODO AHe: Error handling? Permission etc
    p.setAge(p.getAge() - 1);
  }
}
