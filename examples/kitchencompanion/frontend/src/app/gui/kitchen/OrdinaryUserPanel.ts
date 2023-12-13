/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {OrdinaryUserPanelComponent} from '@src/gui/kitchen/OrdinaryUserPanelComponent';
import {GemList} from '@umlp/common';

import {KitchenCompanionManager} from '@src/kitchencompanion/KitchenCompanionManager';
import {User} from '@src/kitchencompanion/User';

import {config} from "@src/gui/kitchen/OrdinaryUserPanelStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'ordinary-user-panel',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/kitchen/OrdinaryUserPanel.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./OrdinaryUserPanel.scss',]
})
export class OrdinaryUserPanel extends OrdinaryUserPanelComponent  {




}


