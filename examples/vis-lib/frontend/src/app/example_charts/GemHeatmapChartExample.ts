/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemHeatmapChartExampleComponent } from '@src/example_charts/GemHeatmapChartExampleComponent';
import { GemHeatmapChartData, GemHeatmapChartEntry } from '@umlp/charts';

@Component({
  selector: 'gem-heatmap-chart-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemHeatmapChartExample.html'
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
