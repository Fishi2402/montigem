package umlp.backendrte.service.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class CommandRestService {

  @PostMapping(value = "/api", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @CrossOrigin
  public ResponseEntity<String> commandApi(@RequestBody String raw){
      // inject id and participantId
      long id = CommandManager.getFreeCommandId();
      String withId = raw.replaceAll("}\\s*$", ",\"id\":" + id + ", \"participantId\": -1"+ "}");

      // Exec command
      Optional<Command<Object>> opt = CommandObjectMapper.deserializeCommand(withId);
      if (opt.isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      // Return result
      CommandResult<Object> res = CommandManager.executeCommand(opt.get());
      return ResponseEntity.ok(
          CommandObjectMapper.serializeResponse(res)
      );
  }
}
