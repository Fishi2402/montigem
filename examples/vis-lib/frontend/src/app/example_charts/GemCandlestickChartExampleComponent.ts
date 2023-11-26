
import { GemCandlestickChartExampleComponentTOP } from '@src/example_charts/GemCandlestickChartExampleComponentTOP';
import { Component } from '@angular/core';
import { GemData2CandlestickChart } from '@umlp/charts';
@Component({
  selector: 'GemCandlestickChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemCandlestickChartExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemCandlestickChartExampleComponent extends GemCandlestickChartExampleComponentTOP  {

  data = [
    { label: '2017-01-17', value: [118, 144, 117, 136] },
    { label: '2017-01-18', value: [133, 142, 128, 140] },
    { label: '2017-01-19', value: [138, 140, 118, 123] },
    { label: '2017-01-20', value: [120, 143, 109, 131] }
  ];

}
