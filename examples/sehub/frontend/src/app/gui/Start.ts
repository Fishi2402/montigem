/* (c) https://github.com/MontiCore/monticore */

import { StartTOP } from '@src/gui/StartTOP';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class Start extends StartTOP {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
  }

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    }
  }

}


