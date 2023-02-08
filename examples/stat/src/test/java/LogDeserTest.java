import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import log_mc_jar.*;
import log_mc_jar.custom_deser.CustomMCJarCLIParameterDeSer;
import log_mc_jar.custom_deser.CustomMCJarLogEntryDeser;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogDeserTest {

  @Test
  public void testLogDeserialization() {
    Log_MC_JARMock.init();
    JsonElement jsonElement = JsonParser.parse("{\"grammar\":[\"GrammarFamily.mc4\"],\n" +
        "  \"dstlGen\":true,\n" +
        "  \"script\":true,\n" +
        " \"gemId\":1" +
        "}");
    assertTrue(jsonElement instanceof JsonObject);
    Optional<MCJarCLIParameter> parameterOpt = new MCJarCLIParameterDeSer().deserialize((JsonObject) jsonElement);
    assertTrue(parameterOpt.isPresent());

    assertTrue(parameterOpt.get().getScript());
    assertTrue(parameterOpt.get().getDstlGen());
    assertTrue(parameterOpt.get().getGrammar().contains("GrammarFamily.mc4"));
  }

  @Test
  public void testJarLogEntryDeserialization() {
    Log_MC_JARMock.init();
    JsonElement jsonElement = JsonParser.parse("{\"parameter\":{\"grammar\":[\"GrammarFamily.mc4\"],\n" +
        "  \"dstlGen\":false,\n" +
        "  \"script\":false},\n" +
        "  \"origin\":\"0.0.0.0\",\n" +
        "  \"duration\":19163\n" +
        "}\n");
    assertTrue(jsonElement instanceof JsonObject);
    Optional<MCJarLogEntryBuilder> logOpt = new CustomMCJarLogEntryDeser().deserializeAsMuchAsPossible((JsonObject) jsonElement);

    assertTrue(logOpt.isPresent());
  }

  @Test
  public void testSerialization() {
    Log_MC_JARMock.init();
    MCJarLogEntryBuilder builder = Log_MC_JARManager.mCJarLogEntryBuilder();
    builder.duration(Duration.ZERO)
        .toolVersion("7.5.0-SNAPSHOT")
        .origin(InetAddress.getLoopbackAddress())
        .receivedTime(Instant.now());

    MCJarCLIParameter parameter = Log_MC_JARManager.mCJarCLIParameterBuilder()
        .dstlGen(false).script(false).grammar(Arrays.asList("KeineGrammatik.mc4")).build().get();

    builder.mCJarCLIParameter(parameter);

    // TODO Optional Assocs nicht wirklich Optional
    ErrorEntry errorEntry = Log_MC_JARMock.errorEntryBuilder()
        .code(Optional.empty())
        .message("Doch kein Error.")
        .build().get();

    MCSummary summary = Log_MC_JARMock.mCSummaryBuilder()
        .ast_hook_point_calls(0)
        .ast_node_instances(0)
        .ast_node_types(0)
        .ast_node_visits(0)
        .ast_node_instances(0)
        .code_hook_point_sets(0)
        .code_hook_point_calls(0)
        .ast_hook_point_calls(0)
        .ast_hook_point_sets(0)
        .empty_hook_point_called(0)
        .generated_files(0)
        .template_hook_point_calls(0)
        .template_hook_point_sets(0)
        .empty_hook_point_called(0)
        .empty_hook_point_calls(0)
        .errors(0)
        .max_ast_depth(0)
        .max_template_depth(0)
        .template_includes(0)
        .unused_handwritten_templates(0)
        .used_handwritten_templates(0)
        .used_standard_templates(0)
        .template_writes(0)
        .variable_assignments(0)
        .variables(0)
        .warnings(0)
        .instantiations(0)
        .unused_standard_templates(0)
        .string_hook_point_calls(0)
        .string_hook_point_sets(0)
        .build().get();

    builder.errorEntry(errorEntry);
    builder.mCSummary(summary);
    MCJarLogEntry entry = builder.build().get();

    System.out.println(new MCJarCLIParameterDeSer().serialize(parameter));
    System.out.println(new MCJarLogEntryDeSer().serialize(entry));
    System.out.println(new MCSummaryDeSer().serialize(summary));
    System.out.println(new ErrorEntryDeSer().serialize(errorEntry));

  }

}
