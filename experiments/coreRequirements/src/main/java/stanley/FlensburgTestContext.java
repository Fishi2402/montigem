/* (c) https://github.com/MontiCore/monticore */
package stanley;

// RTE-T

import hardy.FlensburgManager;
import hardy.HumanErrorCollector;
import hardy.PersonErrorCollector;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.common.GeneralErrorCollector;

// Speichert die Instanzen zu allen genutzten static delegate pattern, wie den Managern.
// Durch die activate Methode werden alle diese Instanzen in den entsprechenden Klassen eingesetzt,
// wodurch sich der Ausführungskontext des Codes, z.B. von Commands, ändern lässt.
// Typischerweise gibt es einen Kontext für den Server und einen für jeden Client.
public class FlensburgTestContext {
  private FlensburgManager flensburgManager;
  private CommandManager commandManager;
  private GeneralErrorCollector generalErrorCollector;
  private HumanErrorCollector humanErrorCollector;
  private PersonErrorCollector personErrorCollector;

  public FlensburgTestContext(
      FlensburgManager flensburgManager,
      CommandManager commandManager,
      GeneralErrorCollector generalErrorCollector,
      HumanErrorCollector humanErrorCollector,
      PersonErrorCollector personErrorCollector
  ) {
    this.flensburgManager = flensburgManager;
    this.commandManager = commandManager;
    this.generalErrorCollector = generalErrorCollector;
    this.humanErrorCollector = humanErrorCollector;
    this.personErrorCollector = personErrorCollector;
  }

  public FlensburgManager getFlensburgManager() {
    return flensburgManager;
  }

  public void setFlensburgManager(FlensburgManager flensburgManager) {
    this.flensburgManager = flensburgManager;
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }

  public void setCommandManager(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  public static FlensburgTestContext fromCurrentContext() {
    return new FlensburgTestContext(
        FlensburgManager.getSingleton(),
        CommandManager.getInstance(),
        GeneralErrorCollector.getInstance(),
        HumanErrorCollector.getInstance(),
        PersonErrorCollector.getInstance()
    );
  }

  public void activate() {
    FlensburgManager.initMe(flensburgManager);
    CommandManager.initMe(commandManager);
    GeneralErrorCollector.initMe(generalErrorCollector);
    HumanErrorCollector.initMe(humanErrorCollector);
    PersonErrorCollector.initMe(personErrorCollector);
  }

  public static void clear() {
    FlensburgManager.initMe(null);
    CommandManager.initMe(null);
    GeneralErrorCollector.initMe(null);
    HumanErrorCollector.initMe(null);
    PersonErrorCollector.initMe(null);
  }
}
