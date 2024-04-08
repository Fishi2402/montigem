/* (c) https://github.com/MontiCore/monticore */
package carrental.commands;

import umlp.backendrte.command.StatusResult;

public class ProcessPayment extends ProcessPaymentTOP {
  public ProcessPayment(long rentalTransactionGemId) {
    super(rentalTransactionGemId);
  }

  @Override
  public StatusResult doAction() {
    // TODO
    return null;
  }
}
