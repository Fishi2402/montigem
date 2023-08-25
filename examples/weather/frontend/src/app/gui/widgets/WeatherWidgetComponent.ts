/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { WeatherWidgetComponentTOP } from '@src/gui/widgets/WeatherWidgetComponentTOP';

@Component({
  selector: 'weather-widget',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WeatherWidget.html',
  styles: [":host{ display: nowrap; flex-direction: row }"],
  styleUrls: ['./WeatherWidget.scss']
})

export class WeatherWidgetComponent extends WeatherWidgetComponentTOP {

}


