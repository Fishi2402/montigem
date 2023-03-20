/* (c) https://github.com/MontiCore/monticore */
package stanley.personcarsedit;

import hardy.Person;

// no implementation of error observer, since no component with ?Type parameters present
// i.e. no input fields exist on the page
public class PersonCarsEdit extends PersonCarsEditTOP {

  public PersonCarsEdit(Person p) {
    super(p);
  }

}
