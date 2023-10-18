import { DailyForecastTOP } from "@src/weather/DailyForecastTOP";
import { WeatherState } from "@src/weather/WeatherState";

// TODO: JSweet?
export class DailyForecast extends DailyForecastTOP {
  public override getMinimumTemperature (): number {
    let min = NaN;
    for (let forecast of this.getDailyForecasts()) {
      let cur = forecast.getTemperature();
      if(isNaN(min)){
        min = cur;
      }else if(cur < min){
        min = cur;
      }
    }
    return min;
  }

  public override getMaximumTemperature (): number {
    let max = NaN;
    for (let forecast of this.getDailyForecasts()) {
      let cur = forecast.getTemperature();
      if(isNaN(max)){
        max = cur;
      }else if(cur > max){
        max = cur;
      }
    }
    return max;
  }

  public override getWeatherState (): WeatherState {
    let counts = [0, 0, 0, 0];

    for (let forecast of this.getDailyForecasts()) {
      let i: number = forecast.getWeatherState();
      counts[i] = counts[i] + 1;
    }

    let max = 0;
    let maxIndex = 0;
    for (let i = 0; i < counts.length; i++) {
      let count = counts[i];
      if(count > max){
        max = count;
        maxIndex = i;
      }
    }

    // Javascript is weird
    return WeatherState[WeatherState[maxIndex]];
  }
}
