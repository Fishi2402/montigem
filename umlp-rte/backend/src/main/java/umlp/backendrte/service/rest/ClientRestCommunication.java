/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.rest;

import de.se_rwth.commons.logging.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;

public class ClientRestCommunication {

  String uri = "";

  public ClientRestCommunication(String uri) {
    this.uri = uri;
  }

  public <T>CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
        if (uri == null) {
          Log.warn("Command cannot be sent: URL is not set. Set the url first with 'set-url {url}'");
          return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
        }
        String json = CommandObjectMapper.serializeCommand(command);
        Log.info("Send command to server: " + json, "UMLP API");
        WebClient client = WebClient.create(uri);
        WebClient.ResponseSpec retrieve = client.post().bodyValue(json)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve();
        String result = retrieve.bodyToMono(String.class).block();
        Log.info("Resceived response: " + result, "UMLP API");
        return (CommandResult<T>) CommandObjectMapper.deserializeResponse(result).get();
  }

}
