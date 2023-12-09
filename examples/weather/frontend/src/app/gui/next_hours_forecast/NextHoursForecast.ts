/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { NextHoursForecastComponent } from "@src/gui/next_hours_forecast/NextHoursForecastComponent";
import { config } from "@src/gui/next_hours_forecast/NextHoursForecastStandaloneConfig";
@Component({
  standalone:true,
  imports:[... config.imports],
  selector: 'next-hours-forecast',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/next_hours_forecast/NextHoursForecast.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['NextHoursForecast.scss',]
})
export class NextHoursForecast extends NextHoursForecastComponent {

  public ngOnInit(): void { // Do nothing, especially not loading default data
  }
}
