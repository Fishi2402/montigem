/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemSunburstChartExampleComponent,config } from '@src/example_charts/GemSunburstChartExampleComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemSunburstChartExample extends GemSunburstChartExampleComponent {

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
