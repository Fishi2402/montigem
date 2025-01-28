/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { WeatherWidgetComponent,config } from 'gui/widgets/WeatherWidgetComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class WeatherWidget extends WeatherWidgetComponent {

}


