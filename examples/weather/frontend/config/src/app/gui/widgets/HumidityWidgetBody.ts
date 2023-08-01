/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {HumidityWidgetBodyTOP} from "@src/gui/widgets/HumidityWidgetBodyTOP";

@Component({
  selector: 'humidity-widget-body',
  templateUrl: '../../../../build/appgui/gui/widgets/HumidityWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./humidity-widget-style.scss']
})

export class HumidityWidgetBody extends HumidityWidgetBodyTOP {

  public override getGemText1Value (): string {
    let cur_day = this.getTimeseries().getContainedDays()[0];
    let cur_forecast = cur_day.getDailyForecasts()[0];
    let humidity = cur_forecast.getRealtiveHumidity();
    return "" + humidity + "%";
  }

}


