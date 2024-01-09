/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemHeatmapChartExampleComponent, config } from '@src/example_charts/GemHeatmapChartExampleComponent';
import { GemHeatmapChartData, GemHeatmapChartEntry } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemHeatmapChartExample extends GemHeatmapChartExampleComponent {
  private chartData: GemHeatmapChartData;
  private chartDataHash: string;

  public override getGemHeatmapChart0Data(): GemHeatmapChartData {
    if (!this.persons) {
      return null;
    }
    let entries: GemHeatmapChartEntry[] = [];
    for (let person of this.persons) {
      let entry = {
        name: person.getName(),
        timestamp: person?.getBirthday()?.getTime()
      };
      entries.push(entry);
    }
    let data: GemHeatmapChartData = {
      entries: entries
    }
    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }


}
