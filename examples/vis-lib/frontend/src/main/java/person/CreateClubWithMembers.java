/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.Address;
import domain.Club;
import domain.DomainManager;
import domain.Person;
import domain.PersonBuilder;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CreateClubWithMembers extends CreateClubWithMembersTOP {

  @Override
  public Consumer<Void> createClub(Club arg) {
    return (Void) -> {
      this.getClub().build();
      Router.navigate("/person/ClubMembers/" + this.getClub().getGemId());
    };
  }

  @Override
  public Consumer<Person> updateClubMembers() {
    return newMember -> this.getClub().addMembers(newMember);
  }

  @Override
  public Consumer<Void> createAlice() {
    return (Void) -> {
      Optional<Address> address = DomainManager.addressBuilder()
          .street("Elmstreet")
          .house(13)
          .build();
      PersonBuilder pBuilder = DomainManager.personBuilder();
      pBuilder
          .name("Alice")
          .address(address.get())
          .birthday(LocalDate.now())
          .stocks(new HashSet())
          .aboutMe("");
      this.getClub().addMembers(pBuilder.build().get());
    };
  }

}
