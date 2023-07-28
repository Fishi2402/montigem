package weather;

public class Timeseries extends TimeseriesTOP {



  @Override
  public double getMinimumTemperature() {
    boolean seen = false;
    Double best = null;
    for (DailyForecast containedDay : getContainedDays()) {
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
    for (DailyForecast containedDay : getContainedDays()) {
      Double maximumTemperature = containedDay.getMaximumTemperature();
      if (!seen || maximumTemperature.compareTo(best) > 0) {
        seen = true;
        best = maximumTemperature;
      }
    }
    return seen ? best : Double.NaN;
  }
}
