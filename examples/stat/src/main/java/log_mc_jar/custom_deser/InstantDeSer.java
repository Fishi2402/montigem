/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.custom_deser;

import umlp.backendrte.command.DeSer;

import java.time.Instant;
import java.util.Optional;

public class InstantDeSer implements DeSer<Instant> {
  @Override
  public String serialize(Instant obj) {
    return obj.toString();
  }

  @Override
  public Optional<Instant> deserialize(String str) {
    return Optional.ofNullable(Instant.parse(str));
  }
}
