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
  protected static long participantId = 0;
  protected static boolean connected = false;

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

    clientWebsocketCommunication = new ClientWebsocketCommunication(scheme, url, port, applicationName, apiName, headers);
    Result<Long> init = clientWebsocketCommunication.init();
    if (init.wasSuccessful()) {
      participantId = init.getValue();
      connected = true;
      Log.info("Websocket connection was successfull. Participant ID: " + participantId,
          UMLP_SHELL_API);
    } else {
      connected = false;
      Log.info("Websocket connection was not successfull, please check your parameters",
          UMLP_SHELL_API);
    }
  }

  public Availability isConnected() {
    return connected && clientWebsocketCommunication.isOpen() ? Availability.available() : Availability.unavailable("Connection must be initialized before. Use 'init'");
  }
}
