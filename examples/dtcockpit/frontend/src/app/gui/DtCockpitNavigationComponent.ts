/* (c) https://github.com/MontiCore/monticore */

import {DtCockpitNavigationComponentTOP} from '@src/gui/DtCockpitNavigationComponentTOP';

import {Component} from '@angular/core';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";


@Component({
  selector: 'dt-cockpit-navigation',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/DtCockpitNavigation.html',
  styles: [":host{ display: flex; flex-direction: column; max-width: 25%; flex-basis: 25%; }"],
  styleUrls: []
})
export  class DtCockpitNavigationComponent extends DtCockpitNavigationComponentTOP  {
  ngOnInit(){
    if (!DtcockpitSecurityManager.isSignedIn()) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
    }
    super.ngOnInit();
  }
}


