/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {WindWidgetBodyTOP} from "@src/gui/widgets/WindWidgetBodyTOP";

@Component({
  selector: 'wind-widget-body',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WindWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./WindWidgetBody.scss']
})

export class WindWidgetBody extends WindWidgetBodyTOP {

  public override getGemText2Value (): string {
    if (this.getTimeseries() == null) {
      return "";
    }
    let curDay = this.getTimeseries().getContainedDays()[0];
    let curForecast = curDay?.getDailyForecasts()[0];
    let speed = curForecast?.getWindspeed();
    return "" + speed ?? "";
  }

}


