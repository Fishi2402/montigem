/* (c) https://github.com/MontiCore/monticore */
package example_basic;

import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class GemButtonExample extends GemButtonExampleTOP {

  @Override
  public Consumer<Void> alert() {
    return (Void) -> HTMLUtils.alert("Hello");
  }
}
