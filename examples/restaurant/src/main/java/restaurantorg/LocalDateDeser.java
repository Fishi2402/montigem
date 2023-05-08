/* (c) https://github.com/MontiCore/monticore */
package restaurantorg;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.DeSer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateDeser implements DeSer<LocalDate> {
  @Override
  public String serialize(LocalDate obj) {
    return '"' + DateTimeFormatter.ISO_LOCAL_TIME.format(obj) + '"';
  }

  @Override
  public Optional<LocalDate> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("LocalDate must be deserialized from json string");
      return Optional.empty();
    }

    return Optional.of(LocalDate.parse(str.substring(1, str.length() - 1), DateTimeFormatter.ISO_LOCAL_TIME));
  }
}
