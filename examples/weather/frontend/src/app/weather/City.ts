import { CityTOP } from "@src/weather/CityTOP";
import { Gem, GemList } from "@umlp/common";
import { Timeseries } from "./Timeseries";
import { DailyForecast } from "@src/weather/DailyForecast";
import { WeatherManager } from "@src/weather/WeatherManager";
import { Forecast } from "@src/weather/Forecast";
import { ForecastGemList } from "@src/weather/gemlists/ForecastGemList";
import { App } from "@src/weather/App";

// TODO: JSweet?
export class City extends CityTOP {
  protected _nextDays: Gem<Timeseries> = new Gem<Timeseries>();
  protected _current?: Forecast;

  protected _nextHours: Forecast;

  // TODO: replace with umlp observers
  protected original = {
    nextDays: { value: null, hash: null, calculating: false },
    nextHours: { value: null, hash: null, calculating: false },
    current: { value: null, hash: null, calculating: false }
  }

  public getNextDays(): Timeseries {
    this.calcNextDays();
    return this._nextDays?.get();
  }

  public async calcNextDays() {
    if (this.original.nextDays.calculating || this.original.nextDays.hash != null) {
      return;
    }
    this.original.nextDays.calculating = true;

    const today = new Date();
    today.setHours(0, 0, 0, 0)
    const until = new Date(today);
    until.setDate(until.getDate() + 7);

    let containedDays: Set<DailyForecast> = new Set<DailyForecast>();

    const daysGemList = await WeatherManager.getDailyForecastList()

    const promises = [];
    for (let i = 0; i < daysGemList.size(); i++) {
      promises.push(
        daysGemList.get(i).then(day => {
          const cur = day.getDate();
          if (cur >= today && cur <= until && this === day.getCity()) {
            containedDays.add(day);
          }
        })
      );
    }

    await Promise.all(promises);

    const builder = await WeatherManager.timeseriesBuilder();
    const timeseries = builder
      .containedDays(containedDays)
      .start(today)
      .end(until)
      .build();

    this.original.nextDays.calculating = false;

    const hash = JSON.stringify(builder);
    if (this.original.nextDays.hash === hash) {
      return;
    }
    this.original.nextDays.hash = hash;

    this._nextDays.set(timeseries);
  }

  public getNextHours() {
    this.calcNextHours();
    return this._nextHours;
  }

  public async calcNextHours() {
    if (this.original.nextHours.calculating || this.original.nextHours.hash != null) {
      return;
    }
    this.original.nextHours.calculating = true;

    const forecastList = await WeatherManager.getForecastList();
    const forecastListLoaded = await forecastList.loadAll();
    let smallerForecastList = [];

    const curTime = new Date();
    let in12Hours = new Date();
    in12Hours.setHours(curTime.getHours() + 12);

    for (let forecast of forecastListLoaded) {
      if (forecast.getDate() >= curTime && forecast.getDate() <= in12Hours) {
        smallerForecastList.push(forecast.getGemId())
      }
    }

    this.original.nextHours.calculating = false;

    const value = new ForecastGemList(smallerForecastList);
    const hash = JSON.stringify(value);
    if (this.original.nextHours.hash === hash) {
      return;
    }
    this.original.nextHours.hash = hash;

    this._nextHours = await value.get(0);
  }

  public getCurrent(): Forecast | undefined {
    this.calcCurrent();
    return this._current;
  }

  public async calcCurrent() {
    if (this.original.current.calculating || this.original.current.hash != null) {
      return;
    }
    this.original.current.calculating = true;

    let today = new Date();

    const forecastGemList = await WeatherManager.getForecastList();
    await forecastGemList.loadAll();
    for (let day of forecastGemList) {
      const cur = day.getDate();
      if (cur.getFullYear() == today.getFullYear()
        && cur.getMonth() == today.getMonth()
        && cur.getDate() == today.getDate()) {

        this.original.current.calculating = false;

        const value = day;
        const hash = value.getDate().toISOString();
        if (this.original.current.hash === hash) {
          return;
        }
        this.original.current.hash = hash;

        this._current = value;
        return;
      }
    }
  }

  public async isFavorite(): Promise<boolean> {
    const appGemList: GemList<App> = await WeatherManager.getAppList();
    const app: App = await appGemList.get(0);
    for (const fav of app.getFavorite()) {
      if (fav.getGemId() === this.getGemId()) {
        return true;
      }
    }
    return false;
  }

  public async removeFavorite(): Promise<boolean> {
    const appGemList: GemList<App> = await WeatherManager.getAppList();
    const app: App = await appGemList.get(0);
    return app.removeFavorite2(this);
  }

}
