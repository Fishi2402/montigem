package innerclasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class HelperClass2 {
  Consumer<Void> whatever;

  public HelperClass2() {
    this.whatever = $ -> {};
  }

  public void myNotification() {}
}
