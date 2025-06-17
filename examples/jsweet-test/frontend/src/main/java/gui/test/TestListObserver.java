/* (c) https://github.com/MontiCore/monticore */
package gui.test;

import jsweettest.A;
import jsweettest.B;
import jsweettest.JsweettestManager;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestListObserver extends TestListObserverTOP {

  @Override
  public String concatFromA(A a) {
    String result = "";
    for (B b : a.getBList()) {
      if (!result.isEmpty()) {
        result += ", ";
      }
      result += b.getValue();
    }
    return result;
  }

  @Override
  public String concatFromBs(List<B> bs) {
    String result = "";
    for (B b : bs) {
      if (!result.isEmpty()) {
        result += ", ";
      }
      result += b.getValue();
    }
    return result;
  }

  @Override
  public Consumer<Void> addB(A a) {
    return Void -> {
      B b = JsweettestManager.bBuilder().value("New B " + Math.random() * 100).build().get();
      a.addB(b);
    };
  }
}
