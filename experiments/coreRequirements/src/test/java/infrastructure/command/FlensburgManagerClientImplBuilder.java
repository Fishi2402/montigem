package infrastructure.command;

// GEN4T: For testing

import de.se_rwth.commons.logging.Log;
import hardy.HumanErrorCollector;
import hardy.PersonErrorCollector;
import stanley.FlensburgTestContext;
import umlp.rte.GeneralErrorCollector;

import java.util.Optional;


// Wird gebraucht, da jeder XErrorCollector ersetzbar sein muss, aber auch per default leer sein kann.
// Dadurch bekäme ein gleichwertiger Konstruktor extrem viele Argumente, was die Nutzung erschwert.
public class FlensburgManagerClientImplBuilder {
  protected FlensburgTestContext serverContext;
  protected GeneralErrorCollector generalErrorCollector;
  protected HumanErrorCollector humanErrorCollector;
  protected PersonErrorCollector personErrorCollector;
  protected TestClientCommandManager commandManager;
  protected ClientCommandCommunication commandCommunication;

  public FlensburgManagerClientImplBuilder backendContext(FlensburgTestContext backendContext){
    this.serverContext = backendContext;
    return this;
  }

  public FlensburgManagerClientImplBuilder humanErrorCollector(HumanErrorCollector humanErrorCollector){
    this.humanErrorCollector = humanErrorCollector;
    return this;
  }

  public FlensburgManagerClientImplBuilder personErrorCollector(PersonErrorCollector personErrorCollector){
    this.personErrorCollector = personErrorCollector;
    return this;
  }

  public FlensburgManagerClientImplBuilder generalErrorCollector(GeneralErrorCollector generalErrorCollector){
    this.generalErrorCollector = generalErrorCollector;
    return this;
  }

  public FlensburgManagerClientImplBuilder commandManager(TestClientCommandManager commandManager){
    this.commandManager = commandManager;
    return this;
  }

  public FlensburgManagerClientImplBuilder commandCommunication(
      ClientCommandCommunication commandCommunication){
    this.commandCommunication = commandCommunication;
    return this;
  }

  public Optional<FlensburgTestContext> build(){
    if(serverContext == null){
      Log.error("Can not build FlensburgTestContext without a backend context!");
      return Optional.empty();
    }
    boolean defaultCommandCommunication = commandCommunication == null;

    FlensburgManagerClientImpl res = new FlensburgManagerClientImpl();
    if (commandManager == null) {
      if (defaultCommandCommunication) {
        commandCommunication = getDefaultCommandCommunication(serverContext);
      }
      commandManager = new TestClientCommandManager(commandCommunication);
    }
    FlensburgTestContext flensburgTestContext = new FlensburgTestContext(
        res,
        commandManager,
        generalErrorCollector != null ? generalErrorCollector : new GeneralErrorCollector(),
        humanErrorCollector != null ? humanErrorCollector : new HumanErrorCollector(),
        personErrorCollector != null ? personErrorCollector : new PersonErrorCollector()
    );
    if (defaultCommandCommunication) {
      finalizeCommandCommunication(flensburgTestContext);
    }
    return Optional.of(flensburgTestContext);
  }

  private ClientCommandCommunication getDefaultCommandCommunication(FlensburgTestContext serverContext) {
    return new CommandDirectCommunication(serverContext);
  }

  private void finalizeCommandCommunication(FlensburgTestContext clientContext) {
    if (commandCommunication != null) {
      ((CommandDirectCommunication) commandCommunication).setClientContext(clientContext);
    }
  }

}
