/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemCandlestickChartExampleComponent ,config} from '@src/example_charts/GemCandlestickChartExampleComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemCandlestickChartExample extends GemCandlestickChartExampleComponent {

  data = [
    { label: '2017-01-17', value: [118, 144, 117, 136] },
    { label: '2017-01-18', value: [133, 142, 128, 140] },
    { label: '2017-01-19', value: [138, 140, 118, 123] },
    { label: '2017-01-20', value: [120, 143, 109, 131] }
  ];

}
