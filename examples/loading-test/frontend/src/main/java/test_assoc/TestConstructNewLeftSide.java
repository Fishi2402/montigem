package test_assoc;

import loadingtests.LeftSide;
import loadingtests.commands.AddNewRightSideTo;
import loadingtests.commands.ConstructNewLeftSide;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestConstructNewLeftSide extends TestConstructNewLeftSideTOP {
  @Override
  public Consumer<Void> callConstruct() {
    return unused -> {
      CommandResult<LeftSide> x = CommandManager.executeCommand(new ConstructNewLeftSide("Left Label", "Right Label"));
      System.err.println("Got result: " + x);
      this.setL(x.asSimpleResult().get());
    };
  }
}
