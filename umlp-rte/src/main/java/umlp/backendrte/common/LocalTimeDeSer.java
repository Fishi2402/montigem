package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalTimeDeSer implements umlp.backendrte.command.DeSer<LocalTime> {
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

  @Override
  public String serialize(LocalTime obj) {
    return '"' + formatter.format(obj) + '"';
  }

  @Override
  public Optional<LocalTime> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("LocalTime must be deserialized from json string");
      return Optional.empty();
    }

    String content = str.substring(1, str.length() - 1);
    return Optional.of(LocalTime.from(formatter.parse(content)));
  }
}
