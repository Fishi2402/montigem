/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.request;

// RTE-P

import umlp.backendrte.command.Command;

import java.util.Optional;

// Wird vom Client zum Server geschickt um Informationen abzufragen.
// Hat keinen Effekt auf die Daten.
// Nutzt zur einfacheren Umsetzung dieselbe Infrastruktur wie Commands
public abstract class Request<T> extends Command<T> {

  private final Optional<String> filter;

  protected Request(String commandType) {
    super(commandType);
    this.filter = Optional.empty();
  }

  protected Request(String commandType, String filter) {
    super(commandType);
    this.filter = Optional.of(filter);
  }

  public Optional<String> getFilter() {
    return this.filter;
  }

  @Override
  public boolean isRequest() {
    return true;
  }
}
