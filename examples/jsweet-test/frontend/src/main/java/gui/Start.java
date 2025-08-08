package gui;

import de.se_rwth.commons.logging.Log;
import gui.StartTOP;
import jsweettest.B;
import jsweettest.JsweettestManager;
import jsweettest.A;
import umlp.backendrte.common.GemList;
import umlp.guiutil.GUIUtils;
import umlp.jsweet.extension.annotation.Component;
import umlp.jsweet.extension.annotation.ManualObservers;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component()
public class Start extends StartTOP {

  @Override
  public String getErsterValue() {
    String res = "A: ";
    for (A a : ((List<A>) JsweettestManager.getAList())) {
      res += a.getValue() + ", ";
    }
    List<A> list = JsweettestManager.getAList();
    for (int i = 0; i < list.size(); i++) {
      A a = list.get(i);
    }
    if (res.length() > 3) {
      res = res.substring(0, res.length() - 2);
    }
    return res;
  }

  @Override
  @ManualObservers
  public String getZweiterValue() {
    String res = "B: ";
    for (B b : (List<B>) JsweettestManager.getBList()) {
      res += b.getValue() + " id: " +b.getGemId() + ", ";
    }
    List<B> list = JsweettestManager.getBList();
    for (int i = 0; i < list.size(); i++) {
      B b = list.get(i);
      GUIUtils.registerObserverToDomainObject(b);

      b = GUIUtils.registerObserverToDomainObject(getRandomB(list));
      b = getRandomB(list);
      b.setValue("New Value");
    }
    if (res.length() > 3) {
      res = res.substring(0, res.length() - 2);
    }

    return res;
  }

  private B getRandomB(List<B> bs) {
    for (B b : bs) {
      if (b.getGemId() > Math.random() * 100) {
        return b;
      }
    }
    return null;
  }

  public Consumer<Void> createB() {
    return $ -> JsweettestManager.bBuilder()
        .value("Das ist ein B")
        .build();
  }
}
