import {Component} from '@angular/core';
import {NextHoursForecastTOP} from "@src/gui/next_hours_forecast/NextHoursForecastTOP";

@Component({
  selector: 'next-hours-forecast',
  templateUrl: '../../../../build/appgui/gui/next_hours_forecast/NextHoursForecast.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['NextHoursForecast.scss',]
})
export  class NextHoursForecast extends NextHoursForecastTOP {

  public  ngOnInit (): void{ // Do nothing, especially not loading default data
  }
}
