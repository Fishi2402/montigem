package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.LongList;
import umlp.command.SimpleResult;
import umlp.request.Request;

// Request to get a filtered subset of Addresss
// TODO: implement filter!
public class Address_get_filtered extends Request<LongList> {
  public Address_get_filtered() {
    super("hardy.commands.Address_get_filtered");
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList(FlensburgManager.getAddressList().getIDs());
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
