/* (c) https://github.com/MontiCore/monticore */
package stanley.personedit;

/* HWC-P extends generated java-gui PersonEditTOP, which was generated from this gui-model:
package stanley.personedit;

import hardy.Flensburg.Person;

import umlp.gui.TextInput;
import umlp.gui.NumberInput;
import umlp.gui.Button;

page PersonEdit(Person p) {
  @TextInput(value = p.nickname);
  @NumberInput(value = p.age);
  @Button(text = "Ok", leftClick = navigate);
  @Button(text = "Decrease", leftClick = decreaseAge);
}
*/

import hardy.Person;

public class PersonEdit extends PersonEditTOP {

  public PersonEdit(Person p) {
    super(p);
  }

  @Override
  public void decreaseAge() {
    p.decreaseAge();
  }

  @Override
  public void navigate() {
    // some implementation which translates to standard Angular navigation
    // rte.navigate()
  }

  public void testAsynchronFunction(Person p) {
    Person child = p.getChildren().get(0);
    System.out.println(child.getAge());
  }

}
