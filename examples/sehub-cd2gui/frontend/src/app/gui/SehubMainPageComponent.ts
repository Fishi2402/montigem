/* (c) https://github.com/MontiCore/monticore */

import { SehubMainPageComponentTOP } from '@src/gui/SehubMainPageComponentTOP';

import { Component } from '@angular/core';

import { SehubSecurityManager } from "../sehub/SehubSecurityManager";

@Component({
  selector: 'sehub-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/SehubMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class SehubMainPageComponent extends SehubMainPageComponentTOP {
  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login/LoginPage']);
    }
    super.ngOnInit();
  }
}
