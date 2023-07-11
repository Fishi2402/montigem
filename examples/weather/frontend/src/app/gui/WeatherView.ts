/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartData, GemLineChartEntry } from '@monticore/montigem-charts';
import { WeatherViewTOP } from '@src/gui/WeatherViewTOP';
import { Forecast } from '@src/weather/Forecast';

@Component({
  selector: 'weather-view',
  templateUrl: '../../../build/appgui/gui/WeatherView.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class WeatherView extends WeatherViewTOP {

  protected cache: { chartData: GemLineChartData } = { chartData: null };

  public override getChartData(): GemLineChartData {
    if (!this.getForecasts()) {
      return null;
    }
    if (this.cache.chartData != null
       && this.cache.chartData.entries.length
       && this.cache.chartData.labels.length
    ) {
      return this.cache.chartData;
    }
    let forecasts = this.getForecasts().toArray();

    const list = forecasts.map(f => f.getDate());
    const xAxis = Array.from(new Set(list.sort().map(d => d.toDateString())));

    const m: Map<string, number[]> = new Map();
    for (const f of forecasts) {
      const label = `Coordinate: ${f.getLongitude().toFixed(2)}, ${f.getLatitude().toFixed(2)}`
      if (!m.has(label)) {
        m.set(label, Array(xAxis.length).fill(null));
      }
      const index = xAxis.indexOf(f.getDate().toDateString());
      if (index >= 0) {
        m.get(label)[index] = f.getTemperature();
      }
    }

    const chartEntries = [];
    for (const [key, value] of m) {
      let entry = new GemLineChartEntry();
      entry.data = value;
      entry.label = key;
      chartEntries.push(entry);
    }
    this.cache.chartData = new GemLineChartData(chartEntries, xAxis);
    return this.cache.chartData;
  }

  public override navigateToUpdatePage(row: Forecast): void {
    this.router.navigate(["..", "b", "ForecastUpdate", row.getGemId()]);
  }

}
