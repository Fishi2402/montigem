/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {KitchenPageComponent,config} from '@src/gui/kitchen/KitchenPageComponent';
import {GemList} from '@umlp/common';

import {KitchenCompanionManager} from '@src/kitchencompanion/KitchenCompanionManager';
import {User} from '@src/kitchencompanion/User';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class KitchenPage extends KitchenPageComponent  {

}


