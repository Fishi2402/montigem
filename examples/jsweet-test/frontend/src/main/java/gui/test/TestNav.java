package gui.test;

import jsweettest.TestDataManager;
import umlp.jsweet.extension.annotation.Component;
import umlp.jsweet.extension.annotation.Directive;

import java.util.function.Consumer;

@Component
public class TestNav extends TestNavTOP {
  public Consumer<Void> resetData() {
    return unused -> {
      System.err.println("Sending test data reset command");
      TestDataManager.executeResetTestData();
    };
  }
}
