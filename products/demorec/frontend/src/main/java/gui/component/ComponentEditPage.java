package gui.component;

import demorec.JoinConnectionKind;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class ComponentEditPage extends ComponentEditPageTOP {

  @Override
  public String getConnectionKindName(JoinConnectionKind connectionKind) {
    return connectionKind.getName();
  }

  @Override
  public Consumer<Void> navigateToConnectionKind(JoinConnectionKind kind) {
    return $ -> {
      Router.navigate("gui/connectionkind/JoinConnectionKindEditPage/"+kind.getGemId());
    };
  }
}
