package gui.pack;

import demorec.DemoRecComponent;
import demorec.JoinConnection;
import demorec.JoinConnectionKind;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ConnectionsTree extends ConnectionsTreeTOP {


  @Override
  public String getConnectionKindName(JoinConnectionKind connectionKind) {
    return connectionKind.getName();
  }

  @Override
  public String getConnectionName(JoinConnection connection) {
    return String.valueOf(connection.getGemId());
  }

  @Override
  public Consumer<Void> navigateToConnection(JoinConnection connection) {
    return $ -> {
      Router.navigate("gui","joinconnection", "JoinConnectionEditPage",String.valueOf(connection.getGemId()));
    };
  }
}
