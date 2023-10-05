package gui;

import de.se_rwth.commons.logging.Log;
import jsweettest.JsweettestManager;
import jsweettest.A;
import umlp.backendrte.common.GemList;

public class Start extends StartTOP {
  @Override
  public String getErsterValue() {
    return "Ich bin ein angepasster Test";
  }

  @Override
  public String getZweiterValue() {
    GemList<A> as = JsweettestManager.getAList();
    Log.warn("" + as.size());
    return as.get(0).getValue();
  }
}
