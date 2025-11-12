/* (c) https://github.com/MontiCore/monticore */
package jsweettest;

import jsweettest.gemlists.AGemList;
import umlp.backendrte.common.GemList;

public class AsyncSubNonAsync extends AsyncSubNonAsyncTOP {

  @Override
  public GemList<A> getADerivedGemList() {
    return new AGemList();
  }
}
