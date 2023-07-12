/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartData, GemGaugeChartEntry, GemLineChartData, GemLineChartEntry } from '@monticore/montigem-charts';
import { WeatherViewTOP } from '@src/gui/WeatherViewTOP';
import { Forecast } from '@src/weather/Forecast';

@Component({
  selector: 'weather-view',
  templateUrl: '../../../build/appgui/gui/WeatherView.html',
  styles: [
    ":host { display: flex; flex-direction: column }",
    ":host .gemColumn8 { display: initial }",
    ":host .gemColumn9 { display: initial }"
  ],
  styleUrls: []
})
export class WeatherView extends WeatherViewTOP {

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
    let forecasts = this.getForecasts().toArray();

    const list = forecasts.map(f => f.getDate()).sort((a, b) => a.getTime() - b.getTime());
    const xAxis = Array.from(new Set(list.map(d => d.toDateString())));

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
      entry.data = value;
      entry.label = key;
      chartEntries.push(entry);
    }
    const result = new GemLineChartData(chartEntries, xAxis);
    const hash = JSON.stringify(result);
    if (this.cache.chart.hash != hash) {
      this.cache.chart.hash = hash;
      this.cache.chart.data = result;
    }
    return this.cache.chart.data;
  }

  public override getGemTable2Rows(): Array<any> {
    if (!this.getForecasts()) {
      return null;
    }

    const result = this.getForecasts().toArray();
    const hash = JSON.stringify(result);
    if (this.cache.table.hash != hash) {
      this.cache.table.hash = hash;
      this.cache.table.data = [...result];
    }

    return this.cache.table.data;
  }

  public override navigateToUpdatePage(row: Forecast): void {
    this.router.navigate(["..", "b", "ForecastUpdate", row.getGemId()]);
  }

  public override getWindspeedData(): GemGaugeChartData {
    if (!this.getForecasts()) {
      return null;
    }
    let forecasts = this.getForecasts().toArray();
    const sum = forecasts.reduce((acc, cur) => acc + cur.getWindspeed(), 0);

    let entries = new GemGaugeChartEntry();
    let pp = +(sum / forecasts.length).toFixed(2);

    entries.name = "windspeed";
    entries.type = "Wind speed"
    entries.value = pp;
    if (Number.isNaN(pp)) {
      return null;
    }

    const result = new GemGaugeChartData([entries]);
    const hash = JSON.stringify(result);
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
    let forecasts = this.getForecasts().toArray();
    const sum = forecasts.reduce((acc, cur) => acc + cur.getRealtiveHumidity(), 0);

    let entries = new GemGaugeChartEntry();
    let pp = +(sum / forecasts.length).toFixed(2);
    if (Number.isNaN(pp)) {
      return null;
    }

    entries.name = "humidity";
    entries.type = "Humidity";
    entries.value = pp;

    const result = new GemGaugeChartData([entries]);
    const hash = JSON.stringify(result);
    if (this.cache.humidity.hash != hash) {
      this.cache.humidity.hash = hash;
      this.cache.humidity.data = result;
    }
    return this.cache.humidity.data;
  }


}
