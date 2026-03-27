package gui.component;

import demorec.JointConnectionKind;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class ComponentEditPage extends ComponentEditPageTOP {

  @Override
  public String getConnectionKindName(JointConnectionKind connectionKind) {
    return connectionKind.getName();
  }

  @Override
  public Consumer<Void> navigateToConnectionKind(JointConnectionKind kind) {
    return $ -> {
      Router.navigate("gui/connectionkind/JointConnectionKindEditPage/"+kind.getGemId());
    };
  }
}
