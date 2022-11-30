package stanley.editiereperson;

/* HWC-P extends generated java-gui EditierePersonEditTOP, which was generated from this gui-model:

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

import hardy.Person;

public class EditierePersonEdit extends EditierePersonEditTOP {

  public EditierePersonEdit(Person p) {
    super(p);
  }

  @Override
  public void decreaseAge() {
    p.setAge(p.getAge() - 1);
  }

  public void testAsynchronFunction(Person p) {
    Person child = p.getChildren().get(0);
    System.out.println(child.getAge());
  }

}
