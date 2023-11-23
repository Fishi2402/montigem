/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ForecastUpdateComponent } from '@src/gui/ForecastUpdateComponent';

@Component({
  selector: 'forecast-update',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/ForecastUpdate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ForecastUpdate extends ForecastUpdateComponent {

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
