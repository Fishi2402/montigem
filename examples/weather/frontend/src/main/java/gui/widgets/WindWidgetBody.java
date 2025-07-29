/* (c) https://github.com/MontiCore/monticore */
package gui.widgets;

import weather.DailyForecast;
import weather.Forecast;
import weather.Timeseries;

public class WindWidgetBody extends WindWidgetBodyTOP {
  @Override
  public String getSpeed(Timeseries timeseries) {
    if (timeseries.getContainedDaysList().isEmpty()) {
      return "No data";
    }
    DailyForecast currentDay = timeseries.getContainedDaysList().get(0);
    if (currentDay.getDailyForecastsList().isEmpty()) {
      return "No data";
    }
    Forecast currentForecast = currentDay.getDailyForecasts(0);
    Double speed = currentForecast.getWindspeed();
    return "" + speed + "";
  }
}
