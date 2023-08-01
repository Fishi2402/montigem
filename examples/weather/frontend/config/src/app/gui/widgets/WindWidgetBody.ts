/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { WindWidgetBodyTOP } from '@src/gui/widgets/WindWidgetBodyTOP';

@Component({
  selector: 'wind-widget-body',
  templateUrl: '../../../../build/appgui/gui/widgets/WindWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./wind-widget-style.scss']
})

export class WindWidgetBody extends WindWidgetBodyTOP {

  public override getGemText2Value (): string {
    let cur_day = this.getTimeseries().getContainedDays()[0];
    let cur_forecast = cur_day.getDailyForecasts()[0];
    let speed = cur_forecast.getWindspeed();
    return "" + speed;
  }

}


