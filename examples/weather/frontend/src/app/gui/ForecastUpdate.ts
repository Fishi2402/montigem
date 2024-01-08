/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ForecastUpdateComponent,config } from '@src/gui/ForecastUpdateComponent';
@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
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
