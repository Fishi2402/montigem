/* (c) https://github.com/MontiCore/monticore */
package gui.widgets;

import weather.DailyForecast;
import weather.Forecast;
import weather.Timeseries;

public class HumidityWidgetBody extends HumidityWidgetBodyTOP {
  @Override
  public String getPercentage(Timeseries timeseries) {
    if (timeseries.getContainedDaysList().isEmpty()) {
      return "No data";
    }
    DailyForecast currentDay = timeseries.getContainedDaysList().get(0);
    if (currentDay.getDailyForecastsList().isEmpty()) {
      return "No data";
    }
    Forecast currentForecast = currentDay.getDailyForecasts(0);
    Integer humidity = currentForecast.getRealtiveHumidity();
    return "" + humidity + "%";
  }
}
