/* (c) https://github.com/MontiCore/monticore */
package hardy.flensburg;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.DeSer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;

public class DateDeSer implements DeSer<Date> {

  @Override
  public String serialize(Date obj) {
    TemporalAccessor ta = obj.toInstant();
    return '"' + DateTimeFormatter.ISO_INSTANT.format(ta) + '"';
  }

  @Override
  public Optional<Date> deserialize(String str) {
    try {
      if(!str.matches("\".*\"")){
        Log.warn("Date must be deserialized from json string");
        return Optional.empty();
      }

      TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(str.substring(1, str.length() - 1));
      return Optional.of(Date.from(Instant.from(ta)));
    }catch (DateTimeParseException e){
      Log.warn("Can not parse date!");
      return Optional.empty();
    }
  }
}
