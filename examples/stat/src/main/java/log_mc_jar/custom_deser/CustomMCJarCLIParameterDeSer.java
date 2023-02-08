package log_mc_jar.custom_deser;

import de.monticore.symboltable.serialization.json.JsonObject;
import log_mc_jar.*;

import java.util.Optional;

public class CustomMCJarCLIParameterDeSer extends MCJarCLIParameterDeSer {

  public Optional<MCJarCLIParameter> deserializeAsMuchAsPossible(JsonObject json) {
    MCJarCLIParameterBuilder builder = Log_MC_JARManager.mCJarCLIParameterBuilder();
    builder
        .grammar(deserializeGrammar(json))
        .dstlGen(deserializeDstlGen(json))
        .script(deserializeScript(json));

    return builder.build();
  }
}
