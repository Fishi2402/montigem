package hardy.commands.person;
// RTE-P

import hardy.FlensburgManager;
import hardy.Person;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

// todo: Dieses Command scheint völlig ungeeignet: Wir ersetzen doch keine Personenobjekte
// --> entfernen

import java.util.Optional;

@Deprecated
public class Person_update extends Command {
  protected final Person person;

  protected Person_update(long id, Person person) {
    super(
        id,
        "hardy.Person",
        false
    );
    this.person = person;
  }

  @Override
  public Optional<?> doRun() throws MontiGemError {
    FlensburgManager.deletePerson(person);
    return Optional.empty();
  }
}
