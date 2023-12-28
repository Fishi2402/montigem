/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartExampleComponent ,config} from '@src/example_charts/GemGaugeChartExampleComponent';
import { GemGaugeChartData, GemGaugeChartEntry } from '@umlp/charts';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
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
