/* (c) https://github.com/MontiCore/monticore */
package stanley.personoverview;

import hardy.Person;

import java.util.List;
import java.util.ArrayList;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class PersonOverview extends PersonOverviewTOP {

  public PersonOverview(List<Person> ps) {
    super(ps);
  }

}
