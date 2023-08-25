/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartData, GemGaugeChartEntry, GemLineChartData, GemLineChartEntry } from '@monticore/montigem-charts';
import { WeatherViewComponentTOP } from '@src/gui/WeatherViewComponentTOP';
import { Forecast } from '@src/weather/Forecast';

@Component({
  selector: 'weather-view',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/WeatherView.html',
  styles: [
    ":host { display: flex; flex-direction: column }",
    ":host .gemColumn8 { display: initial }",
    ":host .gemColumn9 { display: initial }"
  ],
  styleUrls: []
})
export class WeatherViewComponent extends WeatherViewComponentTOP {

}
