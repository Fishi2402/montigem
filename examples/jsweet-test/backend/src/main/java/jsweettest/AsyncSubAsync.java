/* (c) https://github.com/MontiCore/monticore */
package jsweettest;

import jsweettest.gemlists.AGemList;
import umlp.backendrte.common.GemList;

public class AsyncSubAsync extends AsyncSubAsyncTOP {
  @Override
  public GemList<A> getADerivedGemList() {
    AGemList as = new AGemList();
    for (A a : JsweettestManager.getAList()) {
      if (a.getValue().isEmpty()) {
        as.add(a);
      }
    }
    return as.build().get();
  }
}
