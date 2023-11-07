/* (c) https://github.com/MontiCore/monticore */

import {DtCockpitNavigationComponentTOP} from '@src/gui/DtCockpitNavigationComponentTOP';

import {Component} from '@angular/core';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";


@Component({
  selector: 'dt-cockpit-navigation',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/DtCockpitNavigation.html',
  styleUrls: ["./DtCockpitNavigationComponent.scss"]
})
export  class DtCockpitNavigationComponent extends DtCockpitNavigationComponentTOP  {

}
