import { GemSunburstChartExampleComponentTOP } from '@src/example_charts/GemSunburstChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemSunburstChart } from '@umlp/charts';

@Component({
  selector: 'GemSunburstChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemSunburstChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemSunburstChartExampleComponent extends GemSunburstChartExampleComponentTOP  {

data = {
  entry: {
    label: "Prozent",
    value: 100,
  },
  subEntries: [
    {
      entry: {
        label: "Teil 1",
        value: 20,
      },
    },
    {
      entry: {
        label: "Teil 2",
        value: 30,
      },
      subEntries: [
        {
          entry: {
            label: "Teil 2.1",
            value: 20,
          },
          subEntries: [],
        },
          {
            entry: {
              label: "Teil 2.2",
              value: 10,
            },
            subEntries: [],
          },
        ],
      },
    ],
  };
}
