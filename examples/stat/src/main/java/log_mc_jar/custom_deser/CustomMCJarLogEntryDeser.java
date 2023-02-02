package log_mc_jar.custom_deser;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import log_mc_jar.*;

import java.util.Optional;

public class CustomMCJarLogEntryDeser extends MCJarLogEntryDeSer {

  public Optional<MCJarLogEntryBuilder> deserializeAsMuchAsPossible(JsonObject json) {
    MCJarLogEntryBuilder builder = Log_MC_JARManager.mCJarLogEntryBuilder();
    builder.setINTERNAL_deser_mode(true);

    if(json.hasMember("toolVersion")) {
      builder.toolVersion(deserializeToolVersion(json));
    }
    if(json.hasMember("duration")) {
      builder.duration(deserializeDuration(json));
    }
    if(json.hasMember("receivedTime")) {
      builder.receivedTime(deserializeReceivedTime(json));
    }
    if(json.hasMember("origin")) {
      builder.origin(deserializeOrigin(json));
    }

    // Associations
    if(json.hasMember("parameter")) {
      Optional<MCJarCLIParameter> cliParameterOptional = new CustomMCJarCLIParameterDeSer().deserializeAsMuchAsPossible((json.getMember("parameter").getAsJsonObject()));
      if(cliParameterOptional.isEmpty()) {
        Log.error("No CLI Parameter given in JSON.");
        return Optional.empty();
      }
      builder.mCJarCLIParameter(cliParameterOptional.get());
    }

    return Optional.of(builder);
  }
}
