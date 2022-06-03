package stanley;

// RTE-T

import hardy.FlensburgManager;
import umlp.command.CommandManager;

// Speichert die Instanzen zu allen genutzten static delegate pattern, wie den Managern.
// Durch die activate Methode werden alle diese Instanzen in den entsprechenden Klassen eingesetzt,
// wodurch sich der Ausführungskontext des Codes, z.B. von Commands, ändern lässt.
// Typischerweise gibt es einen Kontext für den Server und einen für jeden Client.
public class FlensburgTestContext {
  private FlensburgManager flensburgManager;
  private CommandManager commandManager;

  public FlensburgTestContext(FlensburgManager flensburgManager, CommandManager commandManager) {
    this.flensburgManager = flensburgManager;
    this.commandManager = commandManager;
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

  public void activate(){
      FlensburgManager.initMe(flensburgManager);
      CommandManager.initMe(commandManager);
  }
}
