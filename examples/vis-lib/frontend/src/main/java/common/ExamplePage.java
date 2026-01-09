package common;

import umlp.jsweet.extension.annotation.Component;
import java.util.function.Consumer;

@Component
public class ExamplePage extends ExamplePageTOP {

  private void toggleLayoutMode(String mode) {
      if (mode.equals(getLayoutMode())) {
          setLayoutMode("hidden");
      } else {
          setLayoutMode(mode);
      }
  }

  public Consumer<Void> setLayoutFullscreen() {
      return v -> toggleLayoutMode("fullscreen");
  }

  public Consumer<Void> setLayoutRight() {
      return v -> toggleLayoutMode("right");
  }

  public Consumer<Void> setLayoutBottom() {
      return v -> toggleLayoutMode("bottom");
  }

  public Consumer<Void> setLayoutHidden() {
      return v -> setLayoutMode("hidden");
  }
}
