/* (c) https://github.com/MontiCore/monticore */

import { Component, } from '@angular/core';
import { AddToFavoriteTOP } from '@src/gui/AddToFavoriteTOP';
import { WeatherManager } from '@src/weather/WeatherManager';

@Component({
  selector: 'add-to-favorite',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/AddToFavorite.html',
  styles: [":host{ display: flex; flex-direction: column; }", ".gemRow0{ flex-wrap: nowrap; }"],
})

export class AddToFavorite extends AddToFavoriteTOP {

  public searchVal: string = "";
  public options: string[] = [];

  public ngOnInit(): void {
    if (this.app == null) {
      WeatherManager.getAppSure(this.appRoutingId).then(gemPromiseRes => {
        this.app = gemPromiseRes;
      });
    }

    WeatherManager.getCityList().then(gemPromiseRes => {
      this.cities = gemPromiseRes;
      this.cities.loadAll().then(() => {
        for (const city of this.cities) {
          this.options.push(city.getName());
        }
        this.options = this.options.slice();
      });
    });
  }

  public override add2Favorite(): void {
    for (const city of this.getCities()) {
      if (city.getName() === this.searchVal) {
        city.isFavorite().then(isFav => {
          if (!isFav) {
            this.app.addFavorite2(city);
          }
        })
      }
    }
  }

  public override getSearchOptions(): Array<string> {
    return this.options;
  }

  public getSearchEntry(): string {
    return this.searchVal;
  }

  public setSearchEntry(value: string): void {
    this.searchVal = value;
  }

}
