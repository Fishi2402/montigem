package test_assoc;

import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestBiBuilder extends TestBiBuilderTOP {
  @Override
  public Consumer<Void> connectNew(LeftSide l) {
    return unused -> {
      LoadingTestsManager.rightSideBuilder()
          .label("New single right side")
          .connectedLeft(l)
          .build();
    };
  }

  @Override
  public Consumer<Void> connectNewList(LeftSide l) {
    return unused -> {
      LoadingTestsManager.rightSideBuilder()
          .label("New right side in list")
          .connectedLeftList(l)
          .build();
    };
  }
}
