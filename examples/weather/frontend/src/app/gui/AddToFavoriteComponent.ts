/* (c) https://github.com/MontiCore/monticore */

import { Component, } from '@angular/core';
import { AddToFavoriteComponentTOP } from '@src/gui/AddToFavoriteComponentTOP';
import { WeatherManager } from '@src/weather/WeatherManager';

@Component({
  selector: 'add-to-favorite',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/AddToFavorite.html',
  styleUrls: ['./AddToFavorite.scss']
})

export class AddToFavoriteComponent extends AddToFavoriteComponentTOP {

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

}
