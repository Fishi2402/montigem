/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.backendrte.command.LongList;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.request.Request;

// Request to get a filtered subset of Humans
// TODO: implement filter!
public class Human_get_filtered extends Request<LongList> {
  public Human_get_filtered() {
    super("hardy.commands.Human_get_filtered");
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList(FlensburgManager.getHumanList().getIDs());
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
