/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LeadUserPanelComponent} from '@src/gui/kitchen/LeadUserPanelComponent';
import {GemList} from '@umlp/common';

import {KitchenCompanionManager} from '@src/kitchencompanion/KitchenCompanionManager';
import {User} from '@src/kitchencompanion/User';
import {config} from "@src/gui/kitchen/LeadUserPanelStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'lead-user-panel',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/kitchen/LeadUserPanel.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./LeadUserPanel.scss',]
})
export class LeadUserPanel extends LeadUserPanelComponent  {


}


