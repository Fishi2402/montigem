package hardy.commands;

// GEN-P

import hardy.Address;
import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Person_setMainResidence extends Command<Void> {
  protected long personGemId;
  protected long mainResidenceGemId;

  public Person_setMainResidence(long personGemId, long mainResidenceGemId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_setMainResidence"
    );

    this.personGemId = personGemId;
    this.mainResidenceGemId = mainResidenceGemId;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(personGemId);
    if (personOpt.isEmpty()) {
      return StatusResult.error(getId(), new MontiGemError(MontiGemErrorCode.UNKNOWN, null, "Did not find Person with id " + personGemId));
    }

    Optional<Address> addressOpt = FlensburgManager.addressOpt(mainResidenceGemId);
    if (addressOpt.isEmpty()) {
      return StatusResult.error(getId(), new MontiGemError(MontiGemErrorCode.UNKNOWN, null, "Did not find Address with id " + mainResidenceGemId));
    }

    personOpt.get().setMainResidence(addressOpt.get());
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, mainResidenceGemId);
  }
}
