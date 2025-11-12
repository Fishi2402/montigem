/* (c) https://github.com/MontiCore/monticore */

package stanley.personedit;

import hardy.flensburg.Person;

import java.util.function.Consumer;

public class PersonEdit extends PersonEditTOP {

  @Override
  public Consumer<Void> decreaseAge(Person p) {
    return $ -> p.decreaseAge();
  }

}


