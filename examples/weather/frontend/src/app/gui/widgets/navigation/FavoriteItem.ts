/* (c) https://github.com/MontiCore/monticore */

import { Directive } from '@angular/core';
import { FavoriteItemTOP } from '@src/gui/widgets/navigation/FavoriteItemTOP';

@Directive()
export class FavoriteItem extends FavoriteItemTOP {

  public getGemText3Value(): string {
    return this.getFav()?.getCurrent()?.getDate().toDateString();
  }

  public getGemText4Value(): string {
    return (this.getFav()?.getCurrent()?.getWeatherState() ?? '') + '';
  }

  public getGemText6Value(): string {
    return (this.getFav()?.getCurrent()?.getTemperature() ?? '') + '';
  }

  public override removeFavorite(event: any): void {
    this.getFav().removeFavorite();
  }

}


