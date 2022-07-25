package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.SimpleResult;
import umlp.request.Request;

// Gibt die ID der GemList aller Persons zurück
public class Person_get_gemListId extends Request<Long> {

  public Person_get_gemListId() {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_get_gemListId"
    );
  }

  @Override
  public SimpleResult<Long> doRun() {
    return new SimpleResult<>(getId(), FlensburgManager.getPersonList().getGemId(), "java.lang.Long");
  }
}
