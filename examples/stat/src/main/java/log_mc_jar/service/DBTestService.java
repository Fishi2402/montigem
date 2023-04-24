/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.service;

import log_mc_jar.Log_MC_JARManager;
import log_mc_jar.Log_MC_JARManagerImpl;
import log_mc_jar.MCJarCLIParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

// This is a simple test for DB Access
// Feel Free to remove it, once other Commands are also accessing the DB
@RestController
public class DBTestService {
  private static boolean initalized = false;

  @GetMapping("/dbtest/createMCJarCLIParameter/")
  public ResponseEntity<String> createHuman() {
    if(!initalized) {
      Log_MC_JARManagerImpl.init();
    }
    Optional<MCJarCLIParameter> optionalMCJarCLIParameter = Log_MC_JARManager.mCJarCLIParameterBuilder()
        .dstlGen(false)
        .script(true)
        .grammar(Arrays.asList("GrammarA","GrammarB","GrammarC"))
        .build();

    if(optionalMCJarCLIParameter.isPresent()) {
      return ResponseEntity.ok("id:" + optionalMCJarCLIParameter.get().getGemId());
    } else {
      return ResponseEntity.status(400).body("Error Building MCJarCLIParameter");
    }
  }

  @GetMapping("/dbtest/getMCJarCLIParameter/{id}")
  public ResponseEntity<String> increaseAge(@PathVariable long id) {
    if(!initalized) {
      Log_MC_JARManagerImpl.init();
    }
    System.err.println("Got a Request with ID " + id);
    MCJarCLIParameter param = Log_MC_JARManager.getMCJarCLIParameterSure(id);
    param.setScript(false);
    return ResponseEntity.ok("success");
  }

  @GetMapping("/dbtest/getScriptOfParam/{id}")
  public ResponseEntity<String> getAge(@PathVariable long id) {
    if(!initalized) {
      Log_MC_JARManagerImpl.init();
    }
    return ResponseEntity.ok("Script:" + Log_MC_JARManager.getMCJarCLIParameterSure(id).isScript());
  }
}
