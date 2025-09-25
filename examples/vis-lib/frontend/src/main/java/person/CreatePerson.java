/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.DomainManager;
import domain.Person;
import domain.Role;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class CreatePerson extends CreatePersonTOP {

  @Override
  public String getBdayEntry() {
    Person person = this.getPerson();
    if (person != null && person.getBirthday() != null) {
      return person.getBirthday().toString();
    }
    return null;
  }

  @Override
  public void createButtonLeftClick(Void event) {
    Person person = this.getPerson();
    person.setAboutMe("");
    person.setAddress(this.getAddress());
    super.createButtonLeftClick(event);
    this.setPerson(DomainManager.personBuilder());
    this.setAddress(DomainManager.addressBuilder());
//      this.newPerson.emit(person);
      // MH TODO: Wie soll das hier funktionieren?
  }

  // The doCancel method cannot exactly be translated into Java as it requires to return a lambda function which behaves differently in TypeScript, as Event<void> does not have a direct Java counterpart.
  // But here is somewhat a possible interpretation in Java.
  @Override
  public Consumer<Void> doCancel() {
    return (Void) -> {
//      this.cancel.emit(this.getPerson());
      // MH TODO: Wie soll das hier funktionieren?
    };
  }

  @Override
  public List<Option> getRoleOpts() {
    List<Option> roleOptions =new ArrayList<>();
    roleOptions.add(new Option(Role.SYSTEM.toString(),"System"));
    roleOptions.add(new Option(Role.USER.toString(),"User"));
    roleOptions.add(new Option(Role.TOOL.toString(),"Tool"));
    roleOptions.add(new Option(Role.ASSISTANT.toString(),"Assistant"));
    return roleOptions;
  }

  public void foo() {
    HTMLUtils.alert("Created person");
  }
}
