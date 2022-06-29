package hardy.commands;

// GEN-P

import hardy.Address;
import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.LongList;
import umlp.command.SimpleResult;

// Wird von der GemList zurückgegeben von FlensburgManagerClientImpl#getHumanList
// gebraucht, um die Humans später nachzuladen
public class Address_ids extends Command<LongList> {
  public Address_ids() {
    super("hardy.commands.Address_ids");
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList();

    for (Address address : FlensburgManager.getAddressList()) {
      res.add(address.getGemId());
    }
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
