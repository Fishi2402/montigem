package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.SimpleResult;

public class Car_size extends Command<Integer> {
  public Car_size() {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Car_size"
    );
  }

  @Override
  public SimpleResult<Integer> doRun() {
    // TODO: Optimieren, Liste muss nicht ganz geladen werden
    return new SimpleResult<>(getId(), FlensburgManager.getCarList().size(), "java.lang.Integer");
  }

  // undo macht keinen Sinn
}
