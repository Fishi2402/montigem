/* (c) https://github.com/MontiCore/monticore */

import {Component,} from '@angular/core';
import {AddToFavoriteTOP} from '@src/gui/AddToFavoriteTOP';
import {City} from "@src/weather/City";

@Component({
  selector: 'add-to-favorite',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/AddToFavorite.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class AddToFavorite extends AddToFavoriteTOP {

  public ngOnInit(): void {
    super.ngOnInit();
    if (!this.getCity()) {
      this.city = this.app.getCurrentCity();
    }
  }

  public override add2Favorite(event: any): void {
    if (!this.isFavIncluded(this.getCity())) {
      this.app.addFavorite2(this.city);
      this.app._internal_getFavoriteGemList().loadAll();
    }
  }

  public override clearFavorites(event: any): void {
    this.app.clearFavorites();
    this.app._internal_getFavoriteGemList().loadAll().then(citylist => { // TODO: remove if update clarified
      for (const a of citylist) {
        console.log('City ' + a.getName());
      }
    });
  }

  public override getAddbuttonLabel(): string {
    return super.getAddbuttonLabel() + '    ' + this.getFavName();
  }

  private getFavName(): string {
    if (!this.app || !this.city || !this.app.getFavorite() || this.isFavIncluded(this.city)) {
      return '';
    }
    return this.city.getName();
  }

  private isFavIncluded(favCity: City): boolean {
    if (!this.app || !favCity || !this.app.getFavorite()) {
      return false;
    }
    return this.app.containsFavorite(this.city);
  }

}

