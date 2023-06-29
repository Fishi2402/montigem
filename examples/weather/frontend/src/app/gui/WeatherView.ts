/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartData, GemLineChartEntry } from '@monticore/montigem-charts';
import { WeatherViewTOP } from '@src/gui/WeatherViewTOP';

@Component({
  selector: 'weather-view',
  templateUrl: '../../../build/appgui/gui/WeatherView.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class WeatherView extends WeatherViewTOP {

  public override getChartData(): GemLineChartData {
    if (!this.getForecasts()) {
      return null;
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

    return new GemLineChartData(chartEntries, xAxis);
  }

}


