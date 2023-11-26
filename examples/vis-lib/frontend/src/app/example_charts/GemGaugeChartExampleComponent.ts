
import { GemGaugeChartExampleComponentTOP } from '@src/example_charts/GemGaugeChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemGaugeChartData } from '@umlp/charts';
import { GemGaugeChartEntry } from '@umlp/charts';

@Component({
  selector: 'GemGaugeChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemGaugeChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemGaugeChartExampleComponent extends GemGaugeChartExampleComponentTOP  {



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
