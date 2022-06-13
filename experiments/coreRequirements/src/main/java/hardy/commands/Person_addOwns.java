package hardy.commands;

// GEN-P

import hardy.Car;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Person_addOwns extends Command<Void> {
  protected long personGemId;
  protected long carGemId;

  public Person_addOwns(long personGemId, long carGemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_addOwns"
    );

    this.personGemId = personGemId;
    this.carGemId = carGemId;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      return StatusResult.error(getId(), new MontiGemError(MontiGemErrorCode.UNKNOWN, null, "Did not find Person with id " + personGemId));
    }

    Optional<Car> carOpt = FlensburgManager.carOpt(carGemId);
    if (carOpt.isEmpty()) {
      return StatusResult.error(getId(), new MontiGemError(MontiGemErrorCode.UNKNOWN, null, "Did not find Car with id " + carGemId));
    }

    personOpt.get().addOwns(carOpt.get());
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, carGemId);
  }
}
