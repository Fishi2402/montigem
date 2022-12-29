/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

// RTE-P

import umlp.rte.GeneralErrorCollector;

// Command der an Clients gesendet wird um ein GeneralErrorCollector#reportServerError zu synchronisieren
public class ServerError extends Command<Void> {
  protected Throwable t;

  public ServerError(Throwable t) {
    super("umlp.command.ServerError");
    this.t = t;
  }

  @Override
  public StatusResult doRun() {
    GeneralErrorCollector.reportServerError(t);
    return StatusResult.ok(getId());
  }
}
