package test_assoc;

import loadingtests.commands.AddNewRightSideTo;
import loadingtests.commands.Reset;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestAddNewRightSide  extends TestAddNewRightSideTOP {

  @Override
  public Consumer<Void> add(Boolean val_$_$0) {
    return unused -> {
      CommandManager.executeCommand(new AddNewRightSideTo(this.getL().getGemId(), val_$_$0));
    };
  }
}
