package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import hardy.Person;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.command.LongList;
import umlp.rte.Gem;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Person_ids extends Command<LongList> {
  public Person_ids(){
    super(
        "hardy.commands.Person_ids"
    );
  }

  @Override
  public GemResult<LongList> doRun() {
    LongList res = new LongList();

    for (Person person : FlensburgManager.getPersonList()) {
      res.add(person.getGemId());
    }
    return new GemResult<>(getId(), new Gem<>(res), "umlp.command.LongList");
  }
}
