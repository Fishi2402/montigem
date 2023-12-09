/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ForecastCreateComponent } from '@src/gui/ForecastCreateComponent';
import { config } from '@src/gui/ForecastCreateStandaloneConfig';
@Component({
  standalone:true,
  imports:[... config.imports],
  selector: 'forecast-create',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/ForecastCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ForecastCreate extends ForecastCreateComponent {
  public override saveForecast(event: any): void {
    this.build();
  }

  // TODO AGe: date formatting
  public override getGemTextInput2Entry(): string {
    if (!this.getF()) {
      return null;
    }
    let date = this.getF().getDate();
    if (date) {
      return date.toISOString().split('T')[0];
    }
    return null;
  }

}
