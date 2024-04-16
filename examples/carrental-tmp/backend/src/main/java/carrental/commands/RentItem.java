/* (c) https://github.com/MontiCore/monticore */
package carrental.commands;

import carrental.RentalTransaction;
import umlp.backendrte.command.SimpleResult;

public class RentItem extends RentItemTOP {

  public RentItem(long customerGemId, long rentableGemId) {
    super(customerGemId, rentableGemId);
  }

  @Override
  public SimpleResult<RentalTransaction> doAction() {
    // TODO
    return null;
  }
}
