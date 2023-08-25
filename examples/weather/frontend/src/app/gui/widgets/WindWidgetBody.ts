/* (c) https://github.com/MontiCore/monticore */

import { Directive } from '@angular/core';
import {WindWidgetBodyTOP} from "@src/gui/widgets/WindWidgetBodyTOP";

@Directive()

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


