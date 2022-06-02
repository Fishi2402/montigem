package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.rte.Gem;

public class Car_size extends Command<Integer> {
  public Car_size(long commandId) {
    super(
        commandId,
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_size"
    );
  }

  @Override
  public GemResult<Integer> doRun() {
    // TODO: Optimieren, Liste muss nicht ganz geladen werden
    return new GemResult<>(getId(), new Gem<>(FlensburgManager.getCarList().size()), "java.lang.Integer");
  }

  // undo macht keinen Sinn
}
