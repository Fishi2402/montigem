/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTimeDeSer implements umlp.backendrte.command.DeSer<LocalDateTime> {
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Override
  public String serialize(LocalDateTime obj) {
    return '"' + formatter.format(obj) + '"';
  }

  @Override
  public Optional<LocalDateTime> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("LocalDateTime must be deserialized from json string");
      return Optional.empty();
    }

    String content = str.substring(1, str.length() - 1);
    return Optional.of(LocalDateTime.from(formatter.parse(content)));
  }
}
