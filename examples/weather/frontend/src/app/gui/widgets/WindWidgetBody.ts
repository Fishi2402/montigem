/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {WindWidgetBodyComponent,config} from "@src/gui/widgets/WindWidgetBodyComponent";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class WindWidgetBody extends WindWidgetBodyComponent {

  public override getGemText2Value (): string {
    if (this.getTimeseries() == null) {
      return "";
    }
    let curDay = this.getTimeseries().getContainedDaysList()[0];
    let curForecast = curDay?.getDailyForecastsList()[0];
    let speed = curForecast?.getWindspeed();
    return "" + speed ?? "";
  }

}


