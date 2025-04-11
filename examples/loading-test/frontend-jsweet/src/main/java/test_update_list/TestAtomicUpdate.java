package test_update_list;

import loadingtests.commands.ReplaceFoodItem;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class TestAtomicUpdate extends TestAtomicUpdateTOP {

  @Override
  public Consumer<Void> replaceFirst() {
    return unused -> {
      // Send the command when the button is clicked
      CommandManager.executeCommand(new ReplaceFoodItem(this.getUlh().getTestAnimalGem().getGemId()));
    };
  }
}
