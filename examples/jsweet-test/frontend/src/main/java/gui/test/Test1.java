package gui.test;

import jsweettest.JsweettestManager;
import umlp.jsweet.extension.annotation.Component;
import umlp.jsweet.extension.annotation.Directive;

import java.util.function.Consumer;

@Directive(
    cdName = "Jsweettest",
    isEmpty = false,
    isCreate = false
)
@Component
public class Test1 extends Test1TOP {
  public Consumer<Void> addC2() {
    return unused -> {
      JsweettestManager.c2Builder()
          // strings
          .string("C2#fromTest1")
          // ints
          .int1(42)
          .int2(4242)
          .build().get();
    };
  }
}
