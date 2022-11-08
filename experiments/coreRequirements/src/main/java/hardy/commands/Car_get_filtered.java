package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.LongList;
import umlp.command.SimpleResult;
import umlp.request.Request;

// Request to get a filtered subset of Cars
// TODO: implement filter!
public class Car_get_filtered extends Request<LongList> {
  public Car_get_filtered() {
    super("hardy.commands.Car_get_filtered");
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList(FlensburgManager.getCarList().getIDs());
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
