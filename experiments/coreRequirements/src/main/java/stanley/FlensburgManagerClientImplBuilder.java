package stanley;

// GEN4T: For testing

import de.se_rwth.commons.logging.Log;
import hardy.HumanErrorCollector;
import hardy.PersonErrorCollector;
import umlp.command.TestClientCommandManager;
import umlp.rte.GeneralErrorCollector;

import java.util.Optional;


// Wird gebraucht, da jeder XErrorCollector ersetzbar sein muss, aber auch per default leer sein kann.
// Dadurch bekäme ein gleichwertiger Konstruktor extrem viele Argumente, was die Nutzung erschwert.
public class FlensburgManagerClientImplBuilder {
  protected FlensburgTestContext backendContext;
  protected GeneralErrorCollector generalErrorCollector;
  protected HumanErrorCollector humanErrorCollector;
  protected PersonErrorCollector personErrorCollector;
  protected TestClientCommandManager commandManager;

  public FlensburgManagerClientImplBuilder backendContext(FlensburgTestContext backendContext){
    this.backendContext = backendContext;
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

  public Optional<FlensburgTestContext> build(){
    if(backendContext == null){
      Log.error("Can not build FlensburgTestContext without a backend context!");
      return Optional.empty();
    }

    FlensburgManagerClientImpl res = new FlensburgManagerClientImpl();
    TestClientCommandManager testClientCommandManager = commandManager != null ? commandManager : new TestClientCommandManager(backendContext);
    FlensburgTestContext flensburgTestContext = new FlensburgTestContext(
        res,
        testClientCommandManager,
        generalErrorCollector != null ? generalErrorCollector : new GeneralErrorCollector(),
        humanErrorCollector != null ? humanErrorCollector : new HumanErrorCollector(),
        personErrorCollector != null ? personErrorCollector : new PersonErrorCollector()
    );
    testClientCommandManager.setClientContext(flensburgTestContext);
    return Optional.of(flensburgTestContext);
  }


}
