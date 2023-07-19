package log_mc_jar.service;

import de.monticore.generating.templateengine.reporting.commons.StatisticsHandler;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import log_mc_jar.Log_MC_JARManager;
import log_mc_jar.MCJarLogEntry;
import log_mc_jar.MCSummary;
import log_mc_jar.PreJarLogEntry;
import log_mc_jar.PreJarLogEntryBuilder;
import log_mc_jar.UMLPJarCLIParameter;
import log_mc_jar.UMLPJarLogEntry;
import log_mc_jar.UMLPJarLogEntryBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
public class LoggerEndpointService {

  @PostMapping(
      value="/logger",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> log(@RequestBody String body, @RequestHeader("SHASH") String shash, @RequestHeader("STAT-TYPE") String statType) throws UnknownHostException {
    if(!StatisticsHandler.isValidSHASH(shash, body)){
      System.out.println("invalid SHASH");
      return ResponseEntity.ok("");
    }

    JsonObject dataObj = JsonParser.parseJsonObject(body);
    switch (statType) {
      case "UMLP_JAR_JSON":
        log_UMLP_JAR_JSON(dataObj);
        break;
      case "JAR_JSON": // TODO find out the exact string
        //log_MC_JAR_JSON(dataObj);
        break;
      default:
        Log.error("0x3uhsukh34 Unkown Stat-Type: " + statType);
    }
    return ResponseEntity.ok("");
  }

  protected UMLPJarLogEntry log_UMLP_JAR_JSON(JsonObject data) throws UnknownHostException {
    return Log_MC_JARManager.uMLPJarLogEntryBuilder()
        .parameter(parseUmlpJarCLIParameter(data.getObjectMember("parameter")))

        // JarLogEntry
        .duration(Duration.ofMillis(data.getLongMember("duration")))
        .summary(parseSummary(data.getObjectMember("summary"))) // TODO: Optional!
          //TODO: Parse Error

        // PreJarLogEntry
        .uuid(data.getStringMember("uuid"))
        .toolVersion(data.getStringMember("version"))
        .receivedTime(Instant.now())
        .origin(InetAddress.getLoopbackAddress())  // TODO

        .build()
        .get();
  }



  private MCSummary parseSummary(JsonObject data){
    return Log_MC_JARManager.mCSummaryBuilder()
        .errors(data.getIntegerMember("errors"))
        .warnings(data.getIntegerMember("warnings"))
        .generated_files(data.getIntegerMember("generated files"))
        .instantiations(data.getIntegerMember("instantiations"))
        .template_includes(data.getIntegerMember("template includes"))
        .template_writes(data.getIntegerMember("template writes"))
        .used_standard_templates(data.getIntegerMember("used standard templates"))
        .unused_standard_templates(data.getIntegerMember("unused standard templates"))
        .used_handwritten_templates(data.getIntegerMember("used handwritten templates"))
        .unused_handwritten_templates(data.getIntegerMember("unused handwritten templates"))
        .max_template_depth(data.getIntegerMember("max template depth"))
        .max_ast_depth(data.getIntegerMember("max AST depth"))
        .ast_node_instances(data.getIntegerMember("AST node instances"))
        .ast_node_types(data.getIntegerMember("AST node types"))
        .ast_node_visits(data.getIntegerMember("AST node visits"))
        .variables(data.getIntegerMember("variables"))
        .variable_assignments(data.getIntegerMember("variable assignments"))
        .ast_hook_point_sets(data.getIntegerMember("AST hook point sets"))
        .ast_hook_point_calls(data.getIntegerMember("AST hook point calls"))
        .code_hook_point_sets(data.getIntegerMember("code hook point sets"))
        .code_hook_point_calls(data.getIntegerMember("code hook point calls"))
        .template_hook_point_sets(data.getIntegerMember("template hook point sets"))
        .template_hook_point_calls(data.getIntegerMember("template hook point calls"))
        .string_hook_point_sets(data.getIntegerMember("string hook point sets"))
        .string_hook_point_calls(data.getIntegerMember("string hook point calls"))
        .empty_hook_point_called(data.getIntegerMember("empty hook point called"))
        .empty_hook_point_calls(data.getIntegerMember("empty hook point calls"))
        .build()
        .get();
  }

  private UMLPJarCLIParameter parseUmlpJarCLIParameter(JsonObject data){
    return Log_MC_JARManager.uMLPJarCLIParameterBuilder()
        .input(List.of(data.getStringMember("input")))
        .hwc(data.getBooleanMember("hwc"))
        .debug(data.getBooleanMember("debug"))
        .build()
        .get();
  }
}
