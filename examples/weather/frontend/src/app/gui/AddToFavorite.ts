/* (c) https://github.com/MontiCore/monticore */

import { Component, } from '@angular/core';
import { AddToFavoriteComponent ,config} from '@src/gui/AddToFavoriteComponent';
import { WeatherManager } from '@src/weather/WeatherManager';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class AddToFavorite extends AddToFavoriteComponent {

  public searchVal: string = "";
  public options: string[] = [];

  public ngOnInit(): void {
    if (this.app == null) {
      WeatherManager.getApp().then(gemPromiseRes => {
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
