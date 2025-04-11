/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LeadUserPanelComponent,config} from 'gui/kitchen/LeadUserPanelComponent';
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
export class LeadUserPanel extends LeadUserPanelComponent  {


}


