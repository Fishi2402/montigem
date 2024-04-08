/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemScatterPlotExampleComponent, config } from '@src/example_charts/GemScatterPlotExampleComponent';
import { GemScatterPlotData, GemScatterPlotAxis, GemScatterPlotSet, GemScatterPlotPoint, GemScatterPlotShape } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemScatterPlotExample extends GemScatterPlotExampleComponent {
  private chartData: GemScatterPlotData;
  private chartDataHash: string;

  public override getScatterplotData(): GemScatterPlotData {
    if (!this.persons) {
      return null;
    }

    let data: GemScatterPlotData = {
      sets: []
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }
}
