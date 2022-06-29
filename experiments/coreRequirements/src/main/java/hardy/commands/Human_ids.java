package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.LongList;
import umlp.command.SimpleResult;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Human_ids extends Command<LongList> {
  public Human_ids(){
    super(
        "hardy.commands.Human_ids"
    );
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList();

    for (Human human : FlensburgManager.getHumanList()) {
      res.add(human.getGemId());
    }
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }

  // TODO: gemId subscriptions
}
