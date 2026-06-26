package test_html_override;

import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class TestHtmlOverride extends TestHtmlOverrideTOP {
  @Override
  public Consumer<Void> incrementCounter() {
    return $ -> {
      getService().setCounter(getService().getCounter() + 1);
    };
  }
}
