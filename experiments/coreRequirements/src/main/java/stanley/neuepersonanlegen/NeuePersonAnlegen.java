/* (c) https://github.com/MontiCore/monticore */
package stanley.neuepersonanlegen;

// HWC-P

import hardy.PersonBuilder;

public class NeuePersonAnlegen extends NeuePersonAnlegenTOP {
  public NeuePersonAnlegen() {
    super();
  }

  @Override
  protected void decreaseAge(PersonBuilder p) {
    p.setAge(p.getAge() - 1);
  }
}
