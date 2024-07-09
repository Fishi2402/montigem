/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {HumidityWidgetBodyComponent,config} from "@src/gui/widgets/HumidityWidgetBodyComponent";


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class HumidityWidgetBody extends HumidityWidgetBodyComponent {

  public override getGemText1Value (): string {
    let cur_day = this.getTimeseries()?.getContainedDaysList()[0];
    let cur_forecast = cur_day?.getDailyForecastsList()[0];
    let humidity = cur_forecast?.getRealtiveHumidity();
    return "" + humidity ?? "" + "%";
  }

}


