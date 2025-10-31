package test_assoc;

import loadingtests.LeftSide;
import loadingtests.RightSide;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestBidirecti extends TestBidirectiTOP {
  @Override
  public Consumer<Void> connectToLeft(LeftSide l, RightSide r) {
    return unused -> {
      l.setConnectedRight(r);
    };
  }

  @Override
  public Consumer<Void> connectToRight(LeftSide l, RightSide r) {
    return unused -> {
      r.setConnectedLeft(l);
    };
  }

  @Override
  public Consumer<Void> disconnectFromLeft(LeftSide l) {
    return unused -> {
      l.setConnectedRightAbsent();
    };
  }

  @Override
  public Consumer<Void> disconnectFromRight(RightSide r) {
    return unused -> {
      r.setConnectedLeftAbsent();
    };
  }
}
