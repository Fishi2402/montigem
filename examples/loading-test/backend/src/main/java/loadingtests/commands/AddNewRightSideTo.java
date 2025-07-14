package loadingtests.commands;

import loadingtests.LoadingTestsManager;
import loadingtests.RightSide;
import umlp.backendrte.command.SimpleResult;

public class AddNewRightSideTo extends AddNewRightSideToTOP {
  public AddNewRightSideTo(long leftSideGemId, boolean setAssocInBuilder) {
    super(leftSideGemId, setAssocInBuilder);
  }

  @Override
  public SimpleResult<RightSide> doAction() {
    if (this.setAssocInBuilder) {
      // call connectedLeft
      var rightSide = LoadingTestsManager.rightSideBuilder().label("R builder").connectedLeft(LoadingTestsManager.getLeftSideSure(this.getLeftSideGemId())).build();
      return replySuccess(rightSide.get());
    }
    // Call setConnectedLeft after build() call
    var rightSide = LoadingTestsManager.rightSideBuilder().label("R nobuilder").build();
    rightSide.get().setConnectedLeft(LoadingTestsManager.getLeftSideSure(this.getLeftSideGemId()));

    return replySuccess(rightSide.get());
  }
}
