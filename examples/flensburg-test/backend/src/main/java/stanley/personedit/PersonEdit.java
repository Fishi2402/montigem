/* (c) https://github.com/MontiCore/monticore */

package stanley.personedit;

import hardy.flensburg.Person;

public class PersonEdit extends PersonEditTOP {

  public PersonEdit(Person p) {
    super(p);
  }

  @Override
  public void decreaseAge(Object e) {
    p.decreaseAge();
  }

}


