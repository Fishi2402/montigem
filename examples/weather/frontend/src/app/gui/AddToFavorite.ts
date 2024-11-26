/* (c) https://github.com/MontiCore/monticore */

import { Component, } from '@angular/core';
import { AddToFavoriteComponent ,config} from '@src/gui/AddToFavoriteComponent';
import { WeatherManager } from '@src/weather/WeatherManager';
import { Option } from '@umlp/input';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class AddToFavorite extends AddToFavoriteComponent {

  public searchVal: string = "";
  public options: Option[] = [];

  public ngOnInit(): void {
    if (this.app == null) {
      WeatherManager.getApp().then(gemPromiseRes => {
        this.app = gemPromiseRes;
      });
    }

    WeatherManager.getCityList().then(gemPromiseRes => {
      gemPromiseRes.loadAll().then(val => {
        this.cities = val;
        for (const city of this.cities) {
          let value = city.getName();
          this.options.push(new Option(value));
        }
        this.options = this.options.slice();
      });
    });
  }

  public override getSearchOptions(): Array<Option> {
    return this.options;
  }

  public getSearchEntry(): string {
    return this.searchVal;
  }

  public setSearchEntry(value: string): void {
    this.searchVal = value;
  }

}
