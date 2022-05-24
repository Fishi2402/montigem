package stanley.newpersonanlegen;

// HWC-P

import hardy.PersonBuilder;

public class NeuePersonAnlegenGUI extends NeuePersonAnlegenGUITOP {
  public NeuePersonAnlegenGUI() {
    super();
  }

  @Override
  protected void decreaseAge(PersonBuilder p) {
    p.setAge(p.getAge() - 1);
  }
}
