/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {KitchenPageComponent,config} from 'gui/kitchen/KitchenPageComponent';
import {GemList} from '@umlp/common';

import {KitchenCompanionManager} from 'kitchencompanion/KitchenCompanionManager';
import {User} from 'kitchencompanion/User';

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


