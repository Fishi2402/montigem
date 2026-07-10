package innerclasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class HelperClass {
  Consumer<Void> whatever;

  public HelperClass(Consumer<Void> whatever) {
    this.whatever = whatever;
  }

  public void myNotification() {}
}
