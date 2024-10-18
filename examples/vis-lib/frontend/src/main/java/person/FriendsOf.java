/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.Person;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class FriendsOf extends FriendsOfTOP {

  @Override
  public Consumer<Person> updateFriendsList() {
    return newFriend -> this.getPerson().addFriends(newFriend);
  }
}
