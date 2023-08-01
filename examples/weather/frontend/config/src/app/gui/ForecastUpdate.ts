/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ForecastUpdateTOP } from '@src/gui/ForecastUpdateTOP';

@Component({
  selector: 'forecast-update',
  templateUrl: '../../../build/appgui/gui/ForecastUpdate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ForecastUpdate extends ForecastUpdateTOP {

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
