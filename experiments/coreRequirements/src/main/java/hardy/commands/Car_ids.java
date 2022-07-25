package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.LongList;
import umlp.command.SimpleResult;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getCarList
// gebraucht, um die Car später nachzuladen
public class Car_ids extends Command<LongList> {
  protected final int startIncl, endExcl;

  public Car_ids(int startIncl, int endExcl) {
    super("hardy.commands.Car_ids");
    this.startIncl = startIncl;
    this.endExcl = endExcl;
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList();
    res.addAll(FlensburgManager.getCarList().getIDs(startIncl, endExcl));
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
