# Java Client 

You can use the generated MontiGem Java client to connect to a running MontiGem 
server and perform synchronized CRUD operations using the Manager and the usual 
generated domain classes.

```java
package example_analysis;

import de.se_rwth.commons.logging.Log;
import org.springframework.shell.Availability;
import org.springframework.web.socket.WebSocketHttpHeaders;
import domain.DomainManager;
import domain.DomainManagerClientImpl;
import umlp.backendrte.command.ClientCommandManager;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AnalysisClient {
  public static void main(String[] args) {

    DomainManagerClientImpl.init();
    doInit("wss", "montigem.demos.se.rwth-aachen.de", "443", "umlp", "api/command", null, null);

    System.out.println("IS connected? " + isConnected());

    DomainManager.getPersonList().forEach(
        p -> System.out.println(p.getName())
    );
  }

  protected static ClientCommandWebsocketCommunication clientCommandWebsocketCommunication;

  protected static void doInit(String scheme, String url, String port, String applicationName, String apiName, String user, String password) {
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    if (user != null && password != null && !user.isEmpty() && !password.isEmpty()) {
      headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    String uri = buildUri(scheme, url, port, applicationName, apiName);
    clientCommandWebsocketCommunication = new ClientCommandWebsocketCommunication(uri, headers)
        .onCommandReceived(cmd -> {
          String msg = String.format("Received sync command '%s' from server: %s", cmd.getCommandType(), CommandObjectMapper.serializeCommand(cmd));
          Log.info(msg, AnalysisClient.class.getName());
        });
    ClientCommandManager commandManager = new ClientCommandManager(clientCommandWebsocketCommunication);
    if (commandManager.isInitSuccessFul()) {
      ClientCommandManager.initMe(commandManager);
      Log.info(String.format("Websocket connection to server with uri '%s' was successful. Participant ID: %s",
          uri, CommandManager.getParticipantId()), AnalysisClient.class.getName());
    } else {
      Log.info(String.format("Could not establish a websocket connection to server with uri '%s', please check your parameters",
          uri), AnalysisClient.class.getName());
    }
  }

  private static String buildUri(String scheme, String url, String port, String applicationName, String apiName) {
    String portString = port.isEmpty() ? "" : ":" + port;
    String uri = String.format("%s://%s%s/%s/%s", scheme, url, portString, applicationName, apiName);
    return uri;
  }

  public static Availability isConnected() {
    return clientCommandWebsocketCommunication != null && clientCommandWebsocketCommunication.isOpen() ? Availability.available() :
        Availability.unavailable("The Websocket connection must be initialized before. Use 'help init'");
  }
}
```

