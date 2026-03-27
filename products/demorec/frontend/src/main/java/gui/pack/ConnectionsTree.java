package gui.pack;

import demorec.ComponentKind;
import demorec.JointConnection;
import demorec.JointConnectionKind;
import gui.component.ComponentEditPage;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ConnectionsTree extends ConnectionsTreeTOP {


  @Override
  public String getConnectionKindName(JointConnectionKind connectionKind) {
    return connectionKind.getName();
  }

  @Override
  public String getConnectionName(JointConnection connection) {
    return String.valueOf(connection.getGemId());
  }

  @Override
  public Consumer<Void> navigateToConnection(JointConnection connection) {
    return $ -> {
      Router.navigate("gui","jointconnection", "JointConnectionEditPage",String.valueOf(connection.getGemId()));
    };
  }

@Override
  public Consumer<Void> navigateToConnectionKind(JointConnectionKind kind) {
    return $ -> {
      Router.navigate("gui", "connectionkind", "JointConnectionKindEditPage", String.valueOf(kind.getGemId()));
    };
  }

}
