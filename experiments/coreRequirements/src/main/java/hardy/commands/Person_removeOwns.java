package hardy.commands;

// GEN-P

import hardy.Car;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;

import java.util.Optional;

public class Person_removeOwns extends Command<Void> {
  protected long personGemId;
  protected long carGemId;
  public Person_removeOwns(long id, long personGemId, long carGemId) {
    super(
        id,
        "hardy.commands.Person_removeOwns"
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

    personOpt.get().removeOwns(carOpt.get());
    return StatusResult.ok(getId());
  }
}
