package stanley.neuepersonanlegen;

/*
HWC-P extends generated java-gui NeuePersonAnlegenCreateTOP, which was generated from this gui-model:
package stanley.neuepersonanlegen;

import hardy.Flensburg.Person;
import umlp.gui.TextInput;
import umlp.gui.NumberInput;
import umlp.gui.Button;
import stanley.editierepersoncars.EditierePersonCars;

page NeuePersonAnlegen(Person p) { // "leere" Person
  @TextInput(value = p.nickname);
  @NumberInput(value = p.age);
  @Button(text = "Ok", leftClick = navigate(@EditierePersonCars(person=p)));
  @Button(text = "Decrease", leftClick = decreaseAge());
}
 */

public class NeuePersonAnlegenCreate extends NeuePersonAnlegenCreateTOP {

  public NeuePersonAnlegenCreate() {
    super();
  }

  @Override
  public void decreaseAge() {
    pBuilder.setAge(pBuilder.getAge() - 1);
  }
}
