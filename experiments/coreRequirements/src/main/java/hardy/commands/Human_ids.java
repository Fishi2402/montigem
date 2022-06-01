package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Human;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.rte.Gem;

import java.util.ArrayList;
import java.util.List;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Human_ids extends Command<List<Long>> {
  public Human_ids(long id){
    super(
        id,
        "hardy.commands.Human_ids"
    );
  }

  @Override
  public GemResult<List<Long>> doRun() {
    List<Long> res = new ArrayList<>();

    for (Human human : FlensburgManager.getHumanList()) {
      res.add(human.getGemId());
    }
    return new GemResult<>(getId(), new Gem<>(res));
  }
}
