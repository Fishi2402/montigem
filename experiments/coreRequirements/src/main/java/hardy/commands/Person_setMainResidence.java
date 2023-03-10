/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.GeneralErrorCollector;
import umlp.backendrte.common.NotificationScope;

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
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    personOpt.get().setMainResidenceGem(FlensburgManager.createAddressGem(mainResidenceGemId), NotificationScope.LOCAL);
    return StatusResult.ok(getId());
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Arrays.asList(personGemId, mainResidenceGemId);
  }

  public long getPersonGemId() {
    return personGemId;
  }

  public long getMainResidenceGemId() {
    return mainResidenceGemId;
  }
}
