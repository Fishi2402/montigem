/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.custom_deser;

import umlp.backendrte.command.DeSer;

import java.time.Duration;
import java.util.Optional;

public class DurationDeSer implements DeSer<Duration> {
  @Override
  public String serialize(Duration obj) {
    return obj.toString();
  }

  @Override
  public Optional<Duration> deserialize(String str) {
    return Optional.ofNullable(Duration.ofNanos(Long.parseLong(str)));
  }
}
