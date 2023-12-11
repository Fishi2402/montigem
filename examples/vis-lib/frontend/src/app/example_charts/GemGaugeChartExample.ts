/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartExampleComponent } from '@src/example_charts/GemGaugeChartExampleComponent';
import { GemGaugeChartData, GemGaugeChartEntry } from '@umlp/charts';

@Component({
  selector: 'gem-gauge-chart-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemGaugeChartExample.html'
})
export class GemGaugeChartExample extends GemGaugeChartExampleComponent {
  entries: GemGaugeChartEntry[] = [
    {
      name: 'Living Room',
      value: 20,
      type: 'CO2'
    },
    {
      name: 'Bedroom',
      value: 50,
      type: 'CO2'
    },
    {
      name: 'Outside',
      value: -10,
      type: 'CO2'
    }
  ];

  data: GemGaugeChartData = { entries: this.entries };
}
