import {CityTOP} from "@src/weather/CityTOP";
import { Gem } from "@umlp/common";
import { Timeseries } from "./Timeseries";
import {DailyForecast} from "@src/weather/DailyForecast";
import {WeatherManager} from "@src/weather/WeatherManager";
import { Forecast } from "@src/weather/Forecast";

// TODO: JSweet?
export class City extends CityTOP {
  _nextDays: Gem<Timeseries> = new Gem<Timeseries>();
  protected _current?: Forecast;

  public getNextDays(): Timeseries {
    // console.log("getNextDays", this)
    return this.getNextDaysGem().get();
  }

  public getNextDaysGem(): Gem<Timeseries> {
    // console.log("getNextDaysGem1", this)
    this.calcNextDays();
    // console.log("getNextDaysGem2", this)
    return this._nextDays;
  }

  public calcNextDays() {
    if (this._nextDays != null && this._nextDays.isPresent()){
      return;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0)
    const until = new Date();
    until.setDate(until.getDate() + 7);

    let containedDays: Set<DailyForecast> = new Set<DailyForecast>();

    WeatherManager.getDailyForecastList().then(daysGemList => {
      for (let i = 0; i < daysGemList.size(); i++) {
        daysGemList.get(i).then(day => {
          const cur = day.getDate();
          if (cur >= today && cur <= until) {
            containedDays.add(day);
          }
        });
      }

      WeatherManager.timeseriesBuilder()
        .then(builder =>
          this._nextDays.set(
          builder
          .containedDays(containedDays)
          .start(today)
          .end(until)
          .build()
          )
        )
    });
  }

  public getCurrent(): Forecast | undefined {
    return this._current;
  }

  public calcCurrent(): void {
    let today = new Date();

    WeatherManager.getForecastList().then(forecastGemList => {
      for (let day of forecastGemList) {
        const cur = day.getDate();
        if (cur.getFullYear() == today.getFullYear()
            && cur.getMonth() == today.getMonth()
            && cur.getDate() == today.getDate()) {
          this._current = day;
        }
      }
    });
  }

}
