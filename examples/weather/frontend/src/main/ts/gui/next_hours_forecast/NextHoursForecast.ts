/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { NextHoursForecastComponent,config } from "gui/next_hours_forecast/NextHoursForecastComponent";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: '../../../build/appgui/gui/next_hours_forecast/NextHoursForecast.html',
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class NextHoursForecast extends NextHoursForecastComponent {

  public ngOnInit(): void { // Do nothing, especially not loading default data
  }
}
