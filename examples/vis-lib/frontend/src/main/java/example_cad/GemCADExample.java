package example_cad;

import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;
import java.util.function.Consumer;

@Component
public class GemCADExample extends GemCADExampleTOP {

  public Consumer<String> handleNodeClick() {
    return (nodeName) -> {
      if ("red".equals(nodeName)) {
        onRedClick();
      } else if ("blue".equals(nodeName)) {
        HTMLUtils.alert("Blue clicked!");
      } else {
        HTMLUtils.alert("Unknown node clicked: " + nodeName);
      }
    };
  }

  private void onRedClick() {
    HTMLUtils.alert("Hello! The Red Node was clicked (handled via Java Event).");
  }
}
