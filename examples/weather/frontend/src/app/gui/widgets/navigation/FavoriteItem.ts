/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { FavoriteItemComponent,config } from 'gui/widgets/navigation/FavoriteItemComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'favorite-item',
  templateUrl: '../../../../../build/frontendGen/build/appgui/gui/widgets/navigation/FavoriteItem.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./FavoriteItem.scss"]
})
export class FavoriteItem extends FavoriteItemComponent {

  public getGemText3Value(): string {
    return this.getFav()?.getCurrent()?.getDate().toDateString();
  }

  public getGemText4Value(): string {
    return (this.getFav()?.getCurrent()?.getWeatherState() ?? '') + '';
  }

  public getGemText6Value(): string {
    return (this.getFav()?.getCurrent()?.getTemperature() ?? '') + '';
  }

  public override removeFavorite(): (arg: void) => void {
    return $ => {
    // TODO: why does this not work?
      //this.getFav()?.removeFavorite();
    }
  }

}


