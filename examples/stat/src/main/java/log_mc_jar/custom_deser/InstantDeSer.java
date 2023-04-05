/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.custom_deser;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.DeSer;

import java.time.Instant;
import java.util.Optional;

public class InstantDeSer implements DeSer<Instant> {
  @Override
  public String serialize(Instant obj) {
    return '"' + obj.toString() + '"';
  }

  @Override
  public Optional<Instant> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("Instant must be deserialized from json string");
      return Optional.empty();
    }

    return Optional.ofNullable(Instant.parse(str.substring(1, str.length() - 1)));
  }
}
