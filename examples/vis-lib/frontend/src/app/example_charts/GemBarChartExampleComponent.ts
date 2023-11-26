import { GemBarChartExampleComponentTOP } from '@src/example_charts/GemBarChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemBarChartData } from '@umlp/charts';
import { GemBarChartEntry } from '@umlp/charts';

@Component({
  selector: 'GemBarChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemBarChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemBarChartExampleComponent extends GemBarChartExampleComponentTOP  {
 entries: GemBarChartEntry[] = [
  {
    label: "Clothing",
    data: [8000, 7500, 4500, 4000],
  },
  {
    label: "Foods",
    data: [5000, 3500, 4000, 3000],
  },
  {
    label: "Liquids",
    data: [1000, 900, 1000, 5000],
  },
];

 labels: string[] = ["Cherry St.", "Strawberry Mall", "Peach St.", "Apple Rd."];

 data: GemBarChartData = {
  labels: this.labels,
  entries: this.entries
}
}
