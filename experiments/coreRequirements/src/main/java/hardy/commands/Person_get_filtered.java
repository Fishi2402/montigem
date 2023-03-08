/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.backendrte.command.LongList;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.request.Request;

// Request to get a filtered subset of Persons
// TODO: implement filter!
public class Person_get_filtered extends Request<LongList> {
  public Person_get_filtered() {
    super("hardy.commands.Person_get_filtered");
  }

  @Override
  public SimpleResult<LongList> doRun() {
    LongList res = new LongList(FlensburgManager.getPersonList().getIDs());
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
