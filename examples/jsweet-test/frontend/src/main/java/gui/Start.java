package gui;

import de.se_rwth.commons.logging.Log;
import gui.StartTOP;
import jsweettest.B;
import jsweettest.JsweettestManager;
import jsweettest.A;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;

import java.util.stream.Collectors;

@Component()
public class Start extends StartTOP {
  public Start(GemList<A> a) {
    super(a);
  }

  @Override
  public String getErsterValue() {
    String res = "A: ";
    for (A a : JsweettestManager.getAList()) {
      res += a.getValue() + ", ";
    }
    if (res.length() > 3) {
      res = res.substring(0, res.length() - 2);
    }
    return res;
  }

  @Override
  public String getZweiterValue() {
    String res = "B: ";
    for (B b : JsweettestManager.getBList()) {
      res += b.getValue() + " id: " +b.getGemId() + ", ";
    }
    if (res.length() > 3) {
      res = res.substring(0, res.length() - 2);
    }
    return res;
  }

  public void createB(Object event) {
    JsweettestManager.bBuilder()
        .value("Das ist ein B")
        .build();
  }
}
