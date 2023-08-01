/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { WeatherWidgetTOP } from '@src/gui/widgets/WeatherWidgetTOP';

@Component({
  selector: 'weather-widget',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WeatherWidget.html',
  styles: [":host{ display: nowrap; flex-direction: row }"],
  styleUrls: ['./weather-widget-style.scss']
})

export class WeatherWidget extends WeatherWidgetTOP {

}


