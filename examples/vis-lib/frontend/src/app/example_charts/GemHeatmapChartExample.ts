/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemHeatmapChartExampleComponent,config } from '@src/example_charts/GemHeatmapChartExampleComponent';
import { GemHeatmapChartData, GemHeatmapChartEntry } from '@umlp/charts';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemHeatmapChartExample extends GemHeatmapChartExampleComponent {

  entries: GemHeatmapChartEntry[] = [
    {
      name: "Test 2",
      timestamp: 1659001081090
    },
    {
      name: "Test 1",
      timestamp: 1659001080790
    },
  ];
  dummy: GemHeatmapChartData = {
    entries: this.entries
  }

}
