package weather;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class NextDays extends NextDaysTOP {
  protected List<Day> containedDays = new ArrayList<>();

  public NextDays() {
    LocalDate today = LocalDate.now();
    LocalDate until = today.plusDays(7);

    for (Day day : WeatherManager.getDayList()) {
      LocalDate cur = day.getDate();
      if(cur.isEqual(today) || cur.isAfter(today)){
        if(cur.isEqual(until) || cur.isBefore(until)){
          containedDays.add(day);
        }
      }
    }
  }

  @Override
  public int sizeContainedDays() {
    return containedDays.size();
  }

  public List<Day> getContainedDays(){
    return containedDays;
  }

  @Override
  public double getMinimumTemperature() {
    boolean seen = false;
    Double best = null;
    for (Day containedDay : containedDays) {
      Double minimumTemperature = containedDay.getMinimumTemperature();
      if (!seen || minimumTemperature.compareTo(best) < 0) {
        seen = true;
        best = minimumTemperature;
      }
    }
    return seen ? best : Double.NaN;
  }

  @Override
  public double getMaximumTemperature() {
    boolean seen = false;
    Double best = null;
    for (Day containedDay : containedDays) {
      Double maximumTemperature = containedDay.getMaximumTemperature();
      if (!seen || maximumTemperature.compareTo(best) > 0) {
        seen = true;
        best = maximumTemperature;
      }
    }
    return seen ? best : Double.NaN;
  }
}
