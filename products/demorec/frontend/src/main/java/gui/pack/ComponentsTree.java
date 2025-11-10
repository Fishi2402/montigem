package gui.pack;

import demorec.ComponentKind;
import demorec.DemoRecComponent;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ComponentsTree extends ComponentsTreeTOP {

  @Override
  public Consumer<Void> navigateToKind(ComponentKind kind) {
    return $ -> {
      Router.navigate("gui","kind", "EditComponentKindPage",String.valueOf(kind.getGemId()));
    };
  }

  @Override
  public Consumer<Void> navigateToComponent(DemoRecComponent component) {
    return $ -> {
      Router.navigate("gui","component", "ComponentEditPage",String.valueOf(component.getGemId()));
    };
  }
}
