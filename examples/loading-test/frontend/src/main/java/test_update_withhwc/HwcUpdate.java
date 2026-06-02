package test_update_withhwc;

import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class HwcUpdate extends HwcUpdateTOP {
  @Override
  public String myHWCMethod() {
    return "Counter is: " + getCounter();
  }

  @Override
  public Consumer<Void> incrementCounter() {
    return Void -> {
      setCounter(getCounter() + 1);
    };
  }
}
