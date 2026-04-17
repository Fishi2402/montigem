/* (c) https://github.com/MontiCore/monticore */
package test_gemlist_listinterface_sync;


import de.se_rwth.commons.logging.Log;
import loadingtests.LeftSide;
import loadingtests.RightSide;

import java.util.function.Consumer;

public class TestGemListListInterface extends TestGemListListInterfaceTOP {

  @Override
  public Consumer<Void> testAdd(LeftSide s, RightSide r) {
    return $ -> {
      Log.warn("Add is called.");
      s.getConnectedRightListList().add(r);
    };
  }

  @Override
  public Consumer<Void> testRemove(LeftSide s, RightSide r) {
    return $ -> {
      Log.warn("Remove is called.");
      s.getConnectedRightListList().remove(0);
    };
  }

  @Override
  public Consumer<Void> testSet(LeftSide s, RightSide r) {
    return $ -> {
      Log.warn("Set is called.");
      s.getConnectedRightListList().set(0, r);
    };
  }
}
