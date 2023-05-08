/* (c) https://github.com/MontiCore/monticore */
package stanley.personcarsedit;

import hardy.Person;
import umlp.backendrte.gui.Component;

// no implementation of error observer, since no component with ?Type parameters present
// i.e. no input fields exist on the page
@Component
public class PersonCarsEdit extends PersonCarsEditTOP {

  public PersonCarsEdit(Person p) {
    super(p);
  }

}
