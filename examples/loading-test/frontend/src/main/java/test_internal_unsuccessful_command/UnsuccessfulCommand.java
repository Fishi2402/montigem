package test_internal_unsuccessful_command;

import loadingtests.LoadingTestsManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class UnsuccessfulCommand extends UnsuccessfulCommandTOP {

  @Override
  public Consumer<Void> triggerUnsuccessfulCommand() {
    return $ -> {
      LoadingTestsManager.animalOpt(-1);
    };
  }
}
