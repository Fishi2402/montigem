/* (c) https://github.com/MontiCore/monticore */
package loadingtests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadingTestsMock extends LoadingTestsMockTOP {
  @Override
  protected void _doReset() {
    this._TESTING_clearCache();
  }

}
