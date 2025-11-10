/* (c) https://github.com/MontiCore/monticore */
package example_charts;

import domain.Person;
import mc.fenix.charts.gemtimeseriescharttypes.TimeSeriesBuilder;
import mc.fenix.charts.gemtimeseriescharttypes.TimeSeriesData;
import mc.fenix.charts.gemtimeseriescharttypes.TimeSeriesDataBuilder;
import mc.fenix.charts.gemtimeseriescharttypes.TimeSeriesDataPointBuilder;
import mc.fenix.charts.gemtimeseriescharttypes.TimeSeriesEventBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GemTimeSeriesChartExmaple extends GemTimeSeriesChartExmapleTOP {

  @Override
  public TimeSeriesData computeDate(List<Person> persons) {
    TimeSeriesBuilder birthdays = getBirthdays(persons);
    LocalDateTime min1 = LocalDateTime.of(2025, 8, 11, 14, 00, 00);
    LocalDateTime max1 = LocalDateTime.of(2025, 8, 11, 16, 00, 00);
    LocalDateTime min2 = LocalDateTime.of(2025, 8, 11, 13, 00, 00);
    LocalDateTime max2 = LocalDateTime.of(2025, 8, 11, 15, 30, 00);
    TimeSeriesBuilder dataset1 = getRandomLineChartSeries(min1, max1, 25, 30, 50d, 150d, "red");
    TimeSeriesBuilder dataset2 = getRandomLineChartSeries(min2, max2, 75, 100, 45d, 85d, "blue");
    TimeSeriesEventBuilder event1 = getEvent(LocalDateTime.of(2025, 8, 11, 14, 30, 00), "yellow", "black");
    TimeSeriesEventBuilder event2 = getEvent(LocalDateTime.of(2025, 8, 11, 15, 30, 00), "blue", "white");

    TimeSeriesDataBuilder data = new TimeSeriesDataBuilder();
    data.addSeries(dataset1);
    data.addSeries(dataset2);
    data.addEvents(event1);
    data.addEvents(event2);
    return data.build().get();
  }

  private TimeSeriesBuilder getRandomLineChartSeries(LocalDateTime minTime, LocalDateTime maxTime,
      int minDataPoints, int maxDataPoints, Double minValue, Double maxValue, String color) {
    TimeSeriesBuilder chart = new TimeSeriesBuilder()
        .identifier((long) (Math.random() * Long.MAX_VALUE))
        .color(Optional.of(color))
        .name(color + " data set")
        .hidden(false)
        .deactivated(false);

    int numberOfDataPoints = (int) (Math.random() * (maxDataPoints - minDataPoints + 1)) + minDataPoints;
    List<LocalDateTime> dates = new ArrayList<>();
    for (int i = 0; i < numberOfDataPoints; i++) {
      long minTimeEpoch = minTime.toEpochSecond(java.time.ZoneOffset.UTC);
      long maxTimeEpoch = maxTime.toEpochSecond(java.time.ZoneOffset.UTC);
      long randomEpoch = minTimeEpoch + (long) (Math.random() * (maxTimeEpoch - minTimeEpoch));
      dates.add(LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC));
    }
    dates.sort((d1, d2) -> d1.compareTo(d2));
    for (LocalDateTime date : dates) {
      double randomValue = minValue + (Math.random() * (maxValue - minValue));
      chart.addDatapoints(
          new TimeSeriesDataPointBuilder()
              .timestamp(date)
              .value(randomValue).build().get()
      );
    }

    return chart;

  }

  private TimeSeriesBuilder getBirthdays(List<Person> persons) {
    TimeSeriesBuilder birthdays = new TimeSeriesBuilder()
        .identifier(0l)
        .color(Optional.of("blue"))
        .name("Birthdays")
        .hidden(false)
        .deactivated(false);

    for (Person person : persons) {
      LocalDate birthday = person.getBirthday();
      birthdays.addDatapoints(
        new TimeSeriesDataPointBuilder()
            .timestamp(birthday.minusDays(1).atStartOfDay())
            .value(0d).build().get()
      );
      birthdays.addDatapoints(
          new TimeSeriesDataPointBuilder()
              .timestamp(birthday.atStartOfDay())
              .value(1d).build().get()
      );
      birthdays.addDatapoints(
          new TimeSeriesDataPointBuilder()
              .timestamp(birthday.plusDays(1).atStartOfDay())
              .value(0d).build().get()
      );
    }
    return birthdays;
  }

  private  TimeSeriesEventBuilder getEvent(LocalDateTime time, String bgcolor, String textColor) {
    TimeSeriesEventBuilder event = new TimeSeriesEventBuilder();
    event
        .timestamp(time)
        .show(true)
        .backgroundColor(Optional.of(bgcolor))
        .textColor(Optional.of(textColor))
        .name("Random Event");
    return event;
  }
}
