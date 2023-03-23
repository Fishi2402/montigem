/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.shellapi;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.springframework.shell.Availability;
import org.springframework.web.socket.WebSocketHttpHeaders;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.common.Result;
import umlp.backendrte.service.websocket.ClientWebsocketCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UMLPShellAPI {

  private static final String UMLP_SHELL_API = "UMLP Shell API";
  protected static ClientWebsocketCommunication clientWebsocketCommunication;
  protected static long participantId = 0l;

  protected final static CommandResult sendCommandToServerAndReceiveResult(Command command) {
    command.setParticipantId(participantId);
    Log.info("Send command to server: " + CommandObjectMapper.serializeCommand(command),
        UMLP_SHELL_API);
    CommandResult commandResult =
        clientWebsocketCommunication.sendCommandToServerAndReceiveResult(command);
    try {
      Log.info(
          "Received response from server: " + CommandObjectMapper.serializeResponse(commandResult),
          UMLP_SHELL_API);
    } catch (MCFatalError ex) {
      Log.info("Error while executing the command: " + ex.getMessage(), UMLP_SHELL_API);
    }
    return commandResult;
  }

  protected void doInit(String scheme, String url, String port, String applicationName, String apiName, String user, String password) {
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    if (user != null && password != null && !user.isEmpty() && !password.isEmpty()) {
      headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    String uri = buildUri(scheme, url, port, applicationName, apiName);
    clientWebsocketCommunication = new ClientWebsocketCommunication().onCommand(cmd -> {
      String msg = String.format("Received sync command '%s' from server: %s", cmd.getCommandType(), CommandObjectMapper.serializeCommand(cmd));
      Log.info(msg, UMLP_SHELL_API);
        });
    Result<Long> init = clientWebsocketCommunication.init(uri, headers);
    if (init.wasSuccessful()) {
      participantId = init.getValue();
      Log.info(String.format("Websocket connection to '%s' was successfull. Participant ID: %s",
              uri, participantId), UMLP_SHELL_API);
    } else {
      Log.info(String.format("Could not establish a websocket connection to '%s', please check your parameters",
              uri), UMLP_SHELL_API);
    }
  }

  private static String buildUri(String scheme, String url, String port, String applicationName, String apiName) {
    String portString = port.isEmpty() ? "" : ":" + port;
    String uri = String.format("%s://%s%s/%s/%s", scheme, url, portString, applicationName, apiName);
    return uri;
  }

  public Availability isConnected() {
    return clientWebsocketCommunication != null && clientWebsocketCommunication.isOpen() ? Availability.available() :
        Availability.unavailable("The Websocket connection must be initialized before. Use 'help init'");
  }
}
