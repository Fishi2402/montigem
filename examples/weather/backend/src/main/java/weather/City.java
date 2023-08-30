package weather;

import umlp.backendrte.common.Gem;
import weather.gemlists.ForecastGemList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    public ForecastGemList getNextHoursGemList() {
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

    public void calcNextDays() {
        if (_nextDays != null && _nextDays.isPresent()) {
            return;
        }

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

        _nextDays.set(WeatherManager.timeseriesBuilder()
                .containedDays(containedDays)
                .start(today)
                .end(until)
                .build().get());
    }

    /*
    TODO: fix clash with getCurrent() in CityTOP
    public Optional<Forecast> getCurrent() {
        calcCurrent();
        return _current;
    }
    */

    public void calcCurrent() {
        LocalDate today = LocalDate.now();

        for (Forecast day : WeatherManager.getForecastList()) {
            LocalDate cur = day.getDate().toLocalDate();
            if (cur.getYear() == today.getYear()
                    && cur.getMonth() == today.getMonth()
                    && cur.getDayOfMonth() == today.getDayOfMonth()) {
                _current = Optional.of(day);
                return;
            }
        }
    }


    @Override
    public Gem<Timeseries> getNextDaysDerivedGem() {
        //TODO: implement
        return new Gem<>();
    }

    @Override
    public Gem<Forecast> getCurrentDerivedGem() {
        //TODO: implement
        return new Gem<>();
    }

    @Override
    public Gem<Forecast> getNextHoursDerivedGem() {
        //TODO: implement
        return new Gem<>();
    }

}
