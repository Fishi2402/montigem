package gui.test;

import jsweettest.Types;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDateTime;

@Component
public class TestLocalDateTime extends TestLocalDateTimeTOP {

  @Override
  public String getTopGemTextValue(Types t) {
    LocalDateTime tmp = t.getTimestamp();

    tmp = tmp
              .plusYears(1)
              .plusMonths(2)
              .plusWeeks(3)
              .plusDays(4)
              .plusHours(5)
              .plusMinutes(6)
              .plusSeconds(7);

    return tmp.toString();
  }
}
