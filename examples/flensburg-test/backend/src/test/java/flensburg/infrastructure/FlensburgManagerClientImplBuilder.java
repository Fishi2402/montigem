/* (c) https://github.com/MontiCore/monticore */
package flensburg.infrastructure;

// GEN4T: For testing

import de.se_rwth.commons.logging.Log;
import hardy.flensburg.AccountErrorCollector;
import hardy.flensburg.AddressErrorCollector;
import hardy.flensburg.BudgetErrorCollector;
import hardy.flensburg.CarErrorCollector;
import hardy.flensburg.FlensburgManagerClientImpl;
import hardy.flensburg.FlensburgTestContext;
import hardy.flensburg.HumanErrorCollector;
import hardy.flensburg.InvoiceErrorCollector;
import hardy.flensburg.PersonErrorCollector;
import hardy.flensburg.TransactionErrorCollector;
import umlp.backendrte.command.ClientCommandCommunication;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.GeneralErrorCollector;

import java.util.Optional;


// Wird gebraucht, da jeder XErrorCollector ersetzbar sein muss, aber auch per default leer sein kann.
// Dadurch bekäme ein gleichwertiger Konstruktor extrem viele Argumente, was die Nutzung erschwert.
public class FlensburgManagerClientImplBuilder {
  protected FlensburgTestContext serverContext;
  protected GeneralErrorCollector generalErrorCollector;
  protected HumanErrorCollector humanErrorCollector;
  protected CarErrorCollector carErrorCollector;
  protected AddressErrorCollector addressErrorCollector;
  protected TransactionErrorCollector transactionErrorCollector;
  protected AccountErrorCollector accountErrorCollector;
  protected BudgetErrorCollector budgetErrorCollector;
  protected InvoiceErrorCollector invoiceErrorCollector;
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

  public FlensburgManagerClientImplBuilder carErrorCollector(CarErrorCollector carErrorCollector){
    this.carErrorCollector = carErrorCollector;
    return this;
  }
  public FlensburgManagerClientImplBuilder addressErrorCollector(AddressErrorCollector addressErrorCollector){
    this.addressErrorCollector = addressErrorCollector;
    return this;
  }
  public FlensburgManagerClientImplBuilder transactionErrorCollector(TransactionErrorCollector transactionErrorCollector){
    this.transactionErrorCollector = transactionErrorCollector;
    return this;
  }
  public FlensburgManagerClientImplBuilder accountErrorCollector(AccountErrorCollector accountErrorCollector){
    this.accountErrorCollector = accountErrorCollector;
    return this;
  }
  public FlensburgManagerClientImplBuilder budgetErrorCollector(BudgetErrorCollector budgetErrorCollector){
    this.budgetErrorCollector = budgetErrorCollector;
    return this;
  }
  public FlensburgManagerClientImplBuilder invoiceErrorCollector(InvoiceErrorCollector invoiceErrorCollector){
    this.invoiceErrorCollector = invoiceErrorCollector;
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
        carErrorCollector != null ? carErrorCollector : new CarErrorCollector(),
        humanErrorCollector != null ? humanErrorCollector : new HumanErrorCollector(),
        personErrorCollector != null ? personErrorCollector : new PersonErrorCollector(),
        addressErrorCollector != null ? addressErrorCollector : new AddressErrorCollector(),
        accountErrorCollector != null ? accountErrorCollector : new AccountErrorCollector(),
        budgetErrorCollector != null ? budgetErrorCollector : new BudgetErrorCollector(),
        transactionErrorCollector != null ? transactionErrorCollector : new TransactionErrorCollector(),
        invoiceErrorCollector != null ? invoiceErrorCollector : new InvoiceErrorCollector()
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
