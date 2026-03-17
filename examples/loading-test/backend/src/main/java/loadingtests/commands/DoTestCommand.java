package loadingtests.commands;

import jsweet.lang.Erased;
import loadingtests.CommandTestResult;
import loadingtests.LoadingTestsManager;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DoTestCommand extends DoTestCommandTOP {

  @Erased // Why is this Erased necessary :/
  private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

  public DoTestCommand(boolean doBuild) {
    super(doBuild);
  }

  @Override
  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public SimpleResult<CommandTestResult> doAction() {
    CommandTestResult res = LoadingTestsManager.commandTestResultBuilder();

    res.setField("initial");

    if (this.doBuild)
      res.build();

    // In 2 seconds, update the field again
    scheduledExecutorService.schedule(() -> {
      res.setField("updated");
    }, 2, TimeUnit.SECONDS);


    return this.replySuccess(res);
  }
}
