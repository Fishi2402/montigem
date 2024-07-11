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

  private simpleData: GemScatterPlotData;
  private simpleDataHash: string;

  public override getSimpleData() : GemScatterPlotData {
    let greenSet: GemScatterPlotSet = {
      label: "Green data",
      color: "green",
      points: [{
        x: 7,
        y: 5
      },
      {
        x: 9,
        y: 6
      },
      {
        x: 10,
        y: 7
      }]
    }

    let blueSet: GemScatterPlotSet = {
      label: "Blue data",
      color: "blue",
      points: [{
        x: 3,
        y: 3
      }]
    }

    let data: GemScatterPlotData = {
      sets: [greenSet, blueSet]
    }

    let hash: string = JSON.stringify(data);
    if (this.simpleData && this.simpleDataHash === hash) {
      return this.simpleData;
    }

    this.simpleData = data;
    this.simpleDataHash = hash;

    return data;
  }
}
