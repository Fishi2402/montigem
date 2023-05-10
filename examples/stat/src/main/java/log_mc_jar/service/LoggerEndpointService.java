package log_mc_jar.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerEndpointService {

  @PostMapping(
      value="/logger",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> log(@RequestBody String body, @RequestHeader("SHASH") String shash, @RequestHeader("STAT-TYPE") String statType) {
    return ResponseEntity.ok("");
  }
}
