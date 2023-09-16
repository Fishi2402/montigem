/* (c) https://github.com/MontiCore/monticore */

import { StartComponentTOP } from '@src/gui/StartComponentTOP';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class StartComponent extends StartComponentTOP {

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      super.router.navigate(['/login/LoginPage']);
    }
  }

}


