package hardy;

import de.se_rwth.commons.logging.Log;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;

public class DateDeSer implements umlp.command.DeSer<Date> {
  @Override
  public String serialize(Date obj) {
    TemporalAccessor ta = obj.toInstant();
    return DateTimeFormatter.ISO_INSTANT.format(ta);
  }

  @Override
  public Optional<Date> deserialize(String str) {
    try {
      TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(str);
      return Optional.of(Date.from(Instant.from(ta)));
    }catch (DateTimeParseException e){
      Log.warn("Can not parse instant!");
      return Optional.empty();
    }
  }
}
