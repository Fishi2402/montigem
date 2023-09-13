package weather;

import umlp.backendrte.common.Gem;
import umlp.backendrte.common.GemList;
import weather.gemlists.ForecastGemList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class City extends CityTOP {

  //TODO: optimize with local gem cache: -> expiration?
  //Gem<Timeseries> _nextDays = new Gem<>();
  //Optional<Forecast> _current = Optional.empty();

  public ForecastGemList calcNextHours() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime until = today.plusHours(12);

    List<Long> gemIds = new ArrayList<>();

    WeatherManager.getForecastList().forEach(forecast -> {
      var cur = forecast.getDate();
      if (cur.isAfter(today) && cur.isBefore(until)) {
        gemIds.add(forecast.getGemId());
      }
    });

    return new ForecastGemList(gemIds);
  }

  public Optional<Timeseries> calcNextDays() {
    LocalDate today = LocalDate.now();
    LocalDate until = today.plusDays(7);

    // TODO: update forecasts via WeatherReport?

    Set<DailyForecast> containedDays = new HashSet<>();

    for (DailyForecast day : WeatherManager.getDailyForecastList()) {
      LocalDate cur = day.getDate();
      if (cur.isEqual(today) || cur.isAfter(today)) {
        if (cur.isEqual(until) || cur.isBefore(until)) {
          containedDays.add(day);
        }
      }
    }

    return WeatherManager.timeseriesBuilder()
        .containedDays(containedDays)
        .start(today)
        .end(until)
        .build();
  }

  public Optional<Forecast> calcCurrent() {
    LocalDate today = LocalDate.now();
    for (Forecast day : WeatherManager.getForecastList()) {
      LocalDate cur = day.getDate().toLocalDate();
      if (cur.getYear() == today.getYear()
          && cur.getMonth() == today.getMonth()
          && cur.getDayOfMonth() == today.getDayOfMonth()) {
        return Optional.of(day);
      }
    }
    return Optional.empty();
  }


  @Override
  public Gem<Timeseries> getNextDaysDerivedGem() {
    return calcNextDays().map(Gem::new).orElseGet(Gem::new);
  }

  @Override
  public Gem<Forecast> getCurrentDerivedGem() {
    return calcCurrent().map(Gem::new).orElseGet(Gem::new);
  }

  @Override
  public GemList<Forecast> getNextHoursDerivedGemList() {
    //TODO: fix cardinality
    return calcNextHours();
  }
}
