package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.LongList;
import umlp.command.SimpleResult;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Person_ids extends Command<LongList> {
  protected final int startIncl, endExcl;

  public Person_ids(int startIncl, int endExcl){
    super(
        "hardy.commands.Person_ids"
    );
    this.startIncl = startIncl;
    this.endExcl = endExcl;
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList();
    res.addAll(FlensburgManager.getPersonList().getIDs(startIncl, endExcl));
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
