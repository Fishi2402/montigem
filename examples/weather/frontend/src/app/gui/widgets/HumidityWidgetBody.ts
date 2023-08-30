/* (c) https://github.com/MontiCore/monticore */

import { Directive } from '@angular/core';
import {HumidityWidgetBodyTOP} from "@src/gui/widgets/HumidityWidgetBodyTOP";

@Directive()

export class HumidityWidgetBody extends HumidityWidgetBodyTOP {

  public override getGemText1Value (): string {
    let cur_day = this.getTimeseries()?.getContainedDays()[0];
    let cur_forecast = cur_day?.getDailyForecasts()[0];
    let humidity = cur_forecast?.getRealtiveHumidity();
    return "" + humidity ?? "" + "%";
  }

}


