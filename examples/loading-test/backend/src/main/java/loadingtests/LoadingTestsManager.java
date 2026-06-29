/* (c) https://github.com/MontiCore/monticore */
package loadingtests;

import java.util.List;

public abstract class LoadingTestsManager extends LoadingTestsManagerTOP {
  public static void reset() {
    getSingleton()._doReset();
  }

  protected abstract void _doReset();


}
