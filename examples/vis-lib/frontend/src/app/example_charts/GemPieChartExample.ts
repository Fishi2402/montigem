/* (c) https://github.com/MontiCore/monticore */

import { GemPieChartExampleComponent } from '@src/example_charts/GemPieChartExampleComponent';
import { Component } from '@angular/core';
import { GemPieChartData } from '@umlp/charts';
import { GemPieChartEntry } from '@umlp/charts';

@Component({
  selector: 'gem-pie-chart-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemPieChartExample.html',
})
export class GemPieChartExample extends GemPieChartExampleComponent {

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
