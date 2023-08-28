/* (c) https://github.com/MontiCore/monticore */

import { Directive, } from '@angular/core';
import { AddToFavoriteTOP } from '@src/gui/AddToFavoriteTOP';
import { WeatherManager } from '@src/weather/WeatherManager';

@Directive()

export class AddToFavorite extends AddToFavoriteTOP {

  public searchVal: string = "";
  public options: string[] = [];

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
