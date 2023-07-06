package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.DeSer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateDeSer implements DeSer<LocalDate> {
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

  @Override
  public String serialize(LocalDate obj) {
    return '"' + formatter.format(obj) + '"';
  }

  @Override
  public Optional<LocalDate> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("LocalDate must be deserialized from json string");
      return Optional.empty();
    }

    String content = str.substring(1, str.length() - 1);
    return Optional.of(LocalDate.from(formatter.parse(content)));
  }
}
