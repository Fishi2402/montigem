/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { FavoriteItemComponentTOP } from '@src/gui/widgets/navigation/FavoriteItemComponentTOP';

@Component({
  selector: 'favorite-item',
  templateUrl: '../../../../../build/frontendGen/build/appgui/gui/widgets/navigation/FavoriteItem.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./FavoriteItem.scss"]
})
export class FavoriteItemComponent extends FavoriteItemComponentTOP {

}


