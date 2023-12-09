/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { WeatherWidgetComponent } from '@src/gui/widgets/WeatherWidgetComponent';
import { config } from '@src/gui/widgets/WeatherWidgetStandaloneConfig';
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'weather-widget',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WeatherWidget.html',
  styles: [":host{ display: nowrap; flex-direction: row }"],
  styleUrls: ['./WeatherWidget.scss']
})

export class WeatherWidget extends WeatherWidgetComponent {

}


