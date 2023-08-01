package weather;

import umlp.backendrte.common.Gem;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class City extends CityTOP {
  Gem<Timeseries> _nextDays = new Gem<>();
  Optional<Forecast> _current = Optional.empty();

  public Timeseries getNextDays() {
    return getNextDaysGem().get();
  }

  public Gem<Timeseries> getNextDaysGem() {
    calcNextDays();
    return _nextDays;
  }

  public void calcNextDays() {
    if (_nextDays != null && _nextDays.isPresent()){
      return;
    }

    LocalDate today = LocalDate.now();
    LocalDate until = today.plusDays(7);

    // TODO: update forecasts via WeatherReport?

    Set<DailyForecast> containedDays = new HashSet<>();

    for (DailyForecast day : WeatherManager.getDailyForecastList()) {
      LocalDate cur = day.getDate();
      if(cur.isEqual(today) || cur.isAfter(today)){
        if(cur.isEqual(until) || cur.isBefore(until)){
          containedDays.add(day);
        }
      }
    }

    _nextDays.set(WeatherManager.timeseriesBuilder()
        .containedDays(containedDays)
        .start(today)
        .end(until)
        .build().get());
  }

  public Optional<Forecast> getCurrent() {
    calcCurrent();
    return _current;
  }

  public void calcCurrent() {
    LocalDate today = LocalDate.now();

    for (Forecast day : WeatherManager.getForecastList()) {
      LocalDate cur = day.getDate();
      if (cur.getYear() == today.getYear()
          && cur.getMonth() == today.getMonth()
          && cur.getDayOfMonth() == today.getDayOfMonth()) {
        _current = Optional.of(day);
        return;
      }
    }
  }

}
