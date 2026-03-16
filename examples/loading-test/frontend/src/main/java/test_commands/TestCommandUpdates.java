package test_commands;

import loadingtests.CommandTestResult;
import loadingtests.CommandTestResultObserver;
import loadingtests.commands.DoTestCommand;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.common.NotificationScope;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestCommandUpdates extends TestCommandUpdatesTOP {

  @Override
  public Consumer<Void> doTestCommand(Boolean doBuild) {
    return (unused) -> {
      CommandResult<CommandTestResult> resp =  CommandManager.executeCommand(new DoTestCommand(doBuild));

      if (!resp.isSuccessful()) {
        this.setResponseText("!isSuccessful");
        return;
      }
      CommandTestResult result = resp.asSimpleResult().get();
      this.setResponseText(result.getField());

      result.addObserver(new CommandTestResultObserver() {
        @Override
        public void maybeNotifySetField(CommandTestResult commandTestResult, String oldValue, String o, NotificationScope scope) {
          setResponseText(result.getField());
        }
      });

    };
  }
}
