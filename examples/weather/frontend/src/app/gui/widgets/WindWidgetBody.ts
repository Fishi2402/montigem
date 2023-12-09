/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {WindWidgetBodyComponent} from "@src/gui/widgets/WindWidgetBodyComponent";
import {config} from "@src/gui/widgets/WindWidgetBodyStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'wind-widget-body',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WindWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./WindWidgetBody.scss']
})
export class WindWidgetBody extends WindWidgetBodyComponent {

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


