package gui.test;

import jsweettest.C2;
import umlp.jsweet.extension.annotation.Component;
import umlp.jsweet.extension.annotation.Directive;

import java.util.function.Consumer;

@Directive(
    cdName = "Jsweettest",
    isEmpty = false,
    isCreate = false
)
@Component
public class TestC1Comp extends TestC1CompTOP {
  @Override
  public Consumer<Void> deleteC2Opt() {
    return unused -> {
      getC1().setC2optAbsent();
    };
  }

  @Override
  public Consumer<Void> deleteC2MultAssoc(C2 c2) {
    return unused -> {
//      getC1().getC2multList().remove(c2); // TODO: Does not work?
      getC1().removeC2mult(c2);
    };
  }

  @Override
  public Consumer<Void> setC2OptFromAssoc(C2 c2) {
    return unused -> {
      getC1().setC2opt(c2);
    };
  }
}
