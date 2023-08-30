import { Component } from '@angular/core';
import { NextHoursForecastComponentTOP } from "@src/gui/next_hours_forecast/NextHoursForecastComponentTOP";

@Component({
  selector: 'next-hours-forecast',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/next_hours_forecast/NextHoursForecast.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['NextHoursForecast.scss',]
})
export class NextHoursForecastComponent extends NextHoursForecastComponentTOP {

  public ngOnInit(): void { // Do nothing, especially not loading default data
  }
}
