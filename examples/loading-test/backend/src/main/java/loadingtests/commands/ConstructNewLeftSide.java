package loadingtests.commands;

import jsweet.lang.Replace;
import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Timer;
import java.util.TimerTask;

public class ConstructNewLeftSide extends ConstructNewLeftSideTOP {
  public ConstructNewLeftSide(String leftLabel, String rightLabel) {
    super(leftLabel, rightLabel);
  }

  @Override
  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  @Replace("throw new Error('This method can only be called in the backend!');")
  // TODO: WTF do I need this @Replace?!?
  public SimpleResult<LeftSide> doAction() {
    var leftSide = LoadingTestsManager.leftSideBuilder().label(leftLabel).build().get();
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        System.err.println(leftLabel + " updated " + "(" + leftSide.getGemId() + ")");
        leftSide.setLabel(leftLabel + " updated");
        var newRight = LoadingTestsManager.rightSideBuilder().label(rightLabel).build().get();
        leftSide.setConnectedRight(newRight);
      }
    }, 3000);
    return replySuccess(leftSide);
  }
}
