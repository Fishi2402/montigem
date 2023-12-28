/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ForecastCreateComponent,config } from '@src/gui/ForecastCreateComponent';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
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
