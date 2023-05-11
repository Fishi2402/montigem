/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.shellapi;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.springframework.shell.Availability;
import org.springframework.web.socket.WebSocketHttpHeaders;
import umlp.backendrte.command.ClientCommandManager;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.common.BaseClassObjectMapper;
import umlp.backendrte.common.GemBuildable;
import umlp.backendrte.common.GemIdentifiable;
import umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UMLPShellAPI {

  public static final String UMLP_SHELL_API = "UMLP Shell API";
  protected static ClientCommandWebsocketCommunication clientCommandWebsocketCommunication;

  protected void doInit(String scheme, String url, String port, String applicationName, String apiName, String user, String password) {
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    if (user != null && password != null && !user.isEmpty() && !password.isEmpty()) {
      headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    String uri = buildUri(scheme, url, port, applicationName, apiName);
    clientCommandWebsocketCommunication = new ClientCommandWebsocketCommunication(uri, headers)
        .onCommandReceived(cmd -> {
      String msg = String.format("Received sync command '%s' from server: %s", cmd.getCommandType(), CommandObjectMapper.serializeCommand(cmd));
      Log.info(msg, UMLP_SHELL_API);
    });
    ClientCommandManager commandManager = new ClientCommandManager(clientCommandWebsocketCommunication);
    if (commandManager.isInitSuccessFul()) {
      ClientCommandManager.initMe(commandManager);
      Log.info(String.format("Websocket connection to server with uri '%s' was successfull. Participant ID: %s",
          uri, CommandManager.getParticipantId()), UMLP_SHELL_API);
    } else {
      Log.info(String.format("Could not establish a websocket connection to server with uri '%s', please check your parameters",
          uri), UMLP_SHELL_API);
    }
  }

  private static String buildUri(String scheme, String url, String port, String applicationName, String apiName) {
    String portString = port.isEmpty() ? "" : ":" + port;
    String uri = String.format("%s://%s%s/%s/%s", scheme, url, portString, applicationName, apiName);
    return uri;
  }

  public Availability isConnected() {
    return clientCommandWebsocketCommunication != null && clientCommandWebsocketCommunication.isOpen() ? Availability.available() :
        Availability.unavailable("The Websocket connection must be initialized before. Use 'help init'");
  }
}
