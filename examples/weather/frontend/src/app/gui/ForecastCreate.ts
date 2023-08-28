/* (c) https://github.com/MontiCore/monticore */

import { Directive } from '@angular/core';
import { ForecastCreateTOP } from '@src/gui/ForecastCreateTOP';

@Directive()
export class ForecastCreate extends ForecastCreateTOP {
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
