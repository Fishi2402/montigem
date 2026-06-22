package gui.test;

import jsweettest.JsweettestManager;

import java.util.function.Consumer;

public class DropdownInIterate extends DropdownInIterateTOP {
  public Consumer<Void> addRow() {
    return $ -> {
      JsweettestManager.getDropdownInIterateSingleton().addPlaceholders(
          JsweettestManager.placeholderBuilder().build().get()
      );
    };
  }
}
