
import { GemHeatmapChartExampleComponentTOP } from '@src/example_charts/GemHeatmapChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemHeatmapChartData } from '@umlp/charts';
import { GemHeatmapChartEntry } from '@umlp/charts';

@Component({
  selector: 'GemHeatmapChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemHeatmapChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemHeatmapChartExampleComponent extends GemHeatmapChartExampleComponentTOP  {

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
