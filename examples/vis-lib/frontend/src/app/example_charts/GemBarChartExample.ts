/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBarChartExampleComponent } from '@src/example_charts/GemBarChartExampleComponent';
import { GemBarChartData, GemBarChartEntry } from '@umlp/charts';
import {config} from "@src/example_charts/GemBarChartExampleStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'gem-bar-chart-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemBarChartExample.html',
})
export class GemBarChartExample extends GemBarChartExampleComponent {
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
