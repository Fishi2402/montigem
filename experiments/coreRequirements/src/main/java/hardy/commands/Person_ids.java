package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.LongList;
import umlp.command.SimpleResult;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Person_ids extends Command<LongList> {
  public Person_ids(){
    super(
        "hardy.commands.Person_ids"
    );
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList();

    for (Person person : FlensburgManager.getPersonList()) {
      res.add(person.getGemId());
    }
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
