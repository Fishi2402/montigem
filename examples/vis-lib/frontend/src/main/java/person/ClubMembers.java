/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.Person;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ClubMembers extends ClubMembersTOP {

  @Override
  public Consumer<Person> updateClubMembers() {
    return newMember -> getClub().addMembers(newMember);
  }

}
