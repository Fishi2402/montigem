import { GemPieChartExampleComponentTOP } from '@src/example_charts/GemPieChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemPieChartData } from '@umlp/charts';
import { GemPieChartEntry } from '@umlp/charts';

@Component({
  selector: 'GemPieChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemPieChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemPieChartExampleComponent extends GemPieChartExampleComponentTOP  {

 entries: GemPieChartEntry[] = [
  {
    label: "Freizeit",
    value: 100,
  },
  {
    label: "Essen",
    value: 200,
  },
  {
    label: "Miete",
    value: 350,
  },
];
 data: GemPieChartData = {
  entries: this.entries
};
}
