/* (c) https://github.com/MontiCore/monticore */

import { Component, } from '@angular/core';
import { AddToFavoriteComponent ,config} from 'gui/AddToFavoriteComponent';
import { WeatherManager } from 'weather/WeatherManager';
import { Option } from '@umlp/input';
import { AsyncList } from '@umlp/commonj2ts';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class AddToFavorite extends AddToFavoriteComponent {

  public searchVal: string = "";
  public options: AsyncList<Option> = new AsyncList<Option>();

  public ngOnInit(): void {
    if (this.app == null) {
      WeatherManager.getApp().then(gemPromiseRes => {
        this.app = gemPromiseRes;
      });
    }

    WeatherManager.getCityList().then(async val => {
      this.cities = await val.toArraySync();
      for await (const city of this.cities) {
        let value = city.getName();
        this.options.add(await new Option().__OptionConstructor(value));
      }
    });
  }

  public override getSearchOptions(): AsyncList<Option> {
    return this.options;
  }

  public getSearchEntry(): string {
    return this.searchVal;
  }

  public setSearchEntry(value: string): void {
    this.searchVal = value;
  }

}
