/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.Person;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SimpleOverview extends SimpleOverviewTOP {

  private String myVar;

  @Override
  public List<Option> getPersonOptions() {
    List<Option> opts = new ArrayList<Option>();
    for (Person person : this.getPersons()) {
      opts.add(new Option(person.getGemId() + "", person.getName()));
    }
    return opts;
  }

  @Override
  public List<Option> getPotentialFriends() {
    List<Option> opts = new ArrayList<Option>();
    Person currentPerson = null;
    for (Person person : getPersons()) {
      if (this.getPersonId() == person.getGemId()) {
        currentPerson = person;
        break;
      }
    }
    for (Person person : getPersons()) {
      boolean isPotentialFriend = true;
      for (Person friend : currentPerson.getFriendsList()) {
        if (friend.getGemId() == person.getGemId() || currentPerson.getGemId() == person.getGemId()) {
          isPotentialFriend = false;
          break;
        }
      }
      if (isPotentialFriend) {
        opts.add(new Option(person.getGemId() + "", person.getName()));
      }
    }
    return opts;
  }
}
