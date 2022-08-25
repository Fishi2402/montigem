package umlp.request;

// RTE-P

import umlp.command.Command;

// Wird vom Client zum Server geschickt um Informationen abzufragen.
// Hat keinen Effekt auf die Daten.
// Nutzt zur einfacheren Umsetzung dieselbe Infrastruktur wie Commands
public abstract class Request<T> extends Command<T> {
  protected Request(String commandType) {
    super(commandType);
  }

  @Override
  public boolean isRequest() {
    return true;
  }
}
