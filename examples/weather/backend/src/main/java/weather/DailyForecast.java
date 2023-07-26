package weather;

import java.util.ArrayList;
import java.util.List;

public class DailyForecast extends DailyForecastTOP{

  @Override
  public double getMinimumTemperature() {
    double min = Double.NaN;
    for (Forecast forecast : getDailyForecasts()) {
      Double cur = forecast.getTemperature();
      if(Double.isNaN(min)){
        min = cur;
      }else if(cur < min){
        min = cur;
      }
    }
    return min;
  }

  @Override
  public double getMaximumTemperature() {
    double max = Double.NaN;
    for (Forecast forecast : getDailyForecasts()) {
      Double cur = forecast.getTemperature();
      if(Double.isNaN(max)){
        max = cur;
      }else if(cur > max){
        max = cur;
      }
    }
    return max;
  }

  @Override
  public WeatherState getWeatherState() {
    List<Integer> counts = new ArrayList<>();

    for (WeatherState value : WeatherState.values()) {
      counts.add(0);
    }

    for (Forecast forecast : getDailyForecasts()) {
      int i = forecast.getWeatherState().ordinal();
      counts.set(i, counts.get(i) + 1);
    }

    int max = 0;
    int maxIndex = 0;
    for (int i = 0; i < counts.size(); i++) {
      Integer count = counts.get(i);
      if(count > max){
        max = count;
        maxIndex = i;
      }
    }

    return WeatherState.values()[maxIndex];
  }
}
