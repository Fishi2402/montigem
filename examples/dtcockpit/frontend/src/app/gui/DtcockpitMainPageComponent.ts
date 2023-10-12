/* (c) https://github.com/MontiCore/monticore */

import {DtcockpitMainPageComponentTOP} from '@src/gui/DtcockpitMainPageComponentTOP';

import {Component} from '@angular/core';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";

@Component({
  selector: 'dtcockpit-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/DtcockpitMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class DtcockpitMainPageComponent extends DtcockpitMainPageComponentTOP {
  ngOnInit(){
    if (!DtcockpitSecurityManager.isSignedIn()) {
      this.router.navigate(['/login/LoginPage']);
    }
    super.ngOnInit();
  }
}
