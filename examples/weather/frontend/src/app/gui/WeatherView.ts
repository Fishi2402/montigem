/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartData, GemGaugeChartEntry, GemLineChartData, GemLineChartEntry } from '@umlp/charts';
import { WeatherViewComponent,config } from '@src/gui/WeatherViewComponent';
import { Forecast } from '@src/weather/Forecast';
import {StringifyForBuilder} from "@umlp/commonj2ts";
@Component({
  standalone:true,
  imports: [ ...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls,
  styles: [
    ":host { display: flex; flex-direction: column }",
    ":host .gemColumn8 { display: initial }",
    ":host .gemColumn9 { display: initial }"
  ],
})
export class WeatherView extends WeatherViewComponent {

  protected cache: {
    chart: { hash: string, data: GemLineChartData }
    table: { hash: string, data: Forecast[] }
    windspeed: { hash: string, data: GemGaugeChartData }
    humidity: { hash: string, data: GemGaugeChartData }
  } = {
    chart: { hash: null, data: null },
    table: { hash: null, data: null },
    windspeed: { hash: null, data: null },
    humidity: { hash: null, data: null }
  };

  public override getChartData(): GemLineChartData {
    if (!this.getForecasts()) {
      return null;
    }
    let forecasts = this.getForecasts();

    const list = forecasts.map(f => f.getDate()).sort((a, b) => a.getTime() - b.getTime());
    const xAxis: string[] = Array.from(new Set(list.map(d => d.toDateString())));

    const m: Map<string, number[]> = new Map();
    for (const f of forecasts) {
      const label = `Coordinate: ${f.getLongitude().toFixed(2)}, ${f.getLatitude().toFixed(2)}`
      if (!m.has(label)) {
        m.set(label, Array(xAxis.length).fill(null));
      }
      const index = xAxis.indexOf(f.getDate().toDateString());
      if (index >= 0) {
        if (m.get(label)[index] == null) {
          m.get(label)[index] = f.getTemperature();
        } else {
          m.get(label)[index] = (m.get(label)[index] + f.getTemperature()) / 2;
        }
      }
    }

    const chartEntries = [];
    for (const [key, value] of m) {
      let entry = new GemLineChartEntry();
      entry.setDataList(value);
      entry.setLabel(key);
      chartEntries.push(entry);
    }
    const result = new GemLineChartData(chartEntries, xAxis);
    const hash = StringifyForBuilder(result);
    if (this.cache.chart.hash != hash) {
      this.cache.chart.hash = hash;
      this.cache.chart.data = result;
    }
    return this.cache.chart.data;
  }

  public override getForecastsTableRows(): Array<any> {
    if (!this.getForecasts()) {
      return null;
    }

    const result = this.getForecasts();
    const hash = JSON.stringify(result);
    if (this.cache.table.hash != hash) {
      this.cache.table.hash = hash;
      this.cache.table.data = [...result];
    }

    return this.cache.table.data;
  }

  public override navigateToUpdatePage(): (arg: Forecast) => void {
    return (row: Forecast) => this._router.navigate(["..", "gui", "ForecastUpdate", row.getGemId()]);
  }

  public override getWindspeedData(): GemGaugeChartData {
    if (!this.getForecasts()) {
      return null;
    }
    let forecasts = this.getForecasts();
    const sum = forecasts.reduce((acc, cur) => acc + cur.getWindspeed(), 0);

    let entries = new GemGaugeChartEntry();
    let pp = +(sum / forecasts.length).toFixed(2);

    entries.setName("windspeed");
    entries.setType("Wind speed")
    entries.setValue(pp);
    if (Number.isNaN(pp)) {
      return null;
    }

    const result = new GemGaugeChartData([entries]);
    const hash = StringifyForBuilder(result);
    if (this.cache.windspeed.hash != hash) {
      this.cache.windspeed.hash = hash;
      this.cache.windspeed.data = result;
    }
    return this.cache.windspeed.data;
  }

  public override getHumidityData(): GemGaugeChartData {
    if (!this.getForecasts()) {
      return null;
    }
    let forecasts = this.getForecasts();
    const sum = forecasts.reduce((acc, cur) => acc + cur.getRealtiveHumidity(), 0);

    let entries = new GemGaugeChartEntry();
    let pp = +(sum / forecasts.length).toFixed(2);
    if (Number.isNaN(pp)) {
      return null;
    }

    entries.setName("humidity");
    entries.setType("Humidity");
    entries.setValue(pp);

    const result = new GemGaugeChartData([entries]);
    const hash = StringifyForBuilder(result);
    if (this.cache.humidity.hash != hash) {
      this.cache.humidity.hash = hash;
      this.cache.humidity.data = result;
    }
    return this.cache.humidity.data;
  }


}
