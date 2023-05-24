package log_mc_jar.service;

import de.monticore.generating.templateengine.reporting.commons.StatisticsHandler;
import log_mc_jar.Log_MC_JARManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoggerEndpointService {

  @PostMapping(
      value="/logger",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> log(@RequestBody String body, @RequestHeader("SHASH") String shash, @RequestHeader("STAT-TYPE") String statType) {
    if(!StatisticsHandler.isValidSHASH(shash, body)){
      System.out.println("invalid SHASH");
      return ResponseEntity.ok("");
    }

    System.out.println("I AM CALLED!!!");
    System.out.println(body);
    Log_MC_JARManager.errorEntryBuilder()
        .message("Dummy Message")
        .code(Optional.of("code"))
        .build();
    return ResponseEntity.ok("");
  }
}
