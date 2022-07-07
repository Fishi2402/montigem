/* (c) https://github.com/MontiCore/monticore */
package hardy.service;

import de.se_rwth.commons.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingService {

  @GetMapping("/greeting/{msg}")
  public ResponseEntity<String> runCommand(@PathVariable String msg) {
    String res = String.format("Greeting REST hat funktioniert! \n" +
        "Der gesendete Text war: \"%s\".\n" +
        "Einen schönen Tag noch!", msg);
    Log.info(res, "REST");
    return ResponseEntity.ok(res);
  }
}
