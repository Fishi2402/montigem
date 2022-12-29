/* (c) https://github.com/MontiCore/monticore */
package stanley.editiereperson;

/* HWC-P extends generated java-gui EditierePersonCreateTOP, which was generated from this gui-model:
package stanley.editiereperson;

import hardy.Flensburg.Person;
import umlp.gui.TextInput;
import umlp.gui.NumberInput;
import umlp.gui.Button;

import stanley.editierepersoncars.EditierePersonCars;

page EditierePerson(Person p) {
  @TextInput(value = p.nickname);
  @NumberInput(value = p.age);
  @Button(text = "Ok", leftClick = navigate(@EditierePersonCars(person = p)));
  @Button(text = "Decrease", leftClick = decreaseAge());
}   */

public class EditierePersonCreate extends EditierePersonCreateTOP {

  public EditierePersonCreate() {
    super();
  }

  @Override
  public void decreaseAge() {
    pBuilder.setAge(pBuilder.getAge() - 1);
  }

}
