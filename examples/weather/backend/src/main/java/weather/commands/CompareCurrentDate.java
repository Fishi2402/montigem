package weather.commands;

import umlp.backendrte.command.SimpleResult;

import java.time.LocalDateTime;
import java.util.Optional;

public class CompareCurrentDate extends CompareCurrentDateTOP {

  public CompareCurrentDate(Optional<LocalDateTime> date) {
    super(date);
  }

  @Override
  public SimpleResult<String> doAction() {
    return null;
  }
}
