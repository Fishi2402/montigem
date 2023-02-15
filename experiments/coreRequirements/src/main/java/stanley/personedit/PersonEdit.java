/* (c) https://github.com/MontiCore/monticore */
package stanley.personedit;

/* HWC-P extends generated java-gui PersonEditTOP
   generated from gui-model PersonEdit.gui
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

}
