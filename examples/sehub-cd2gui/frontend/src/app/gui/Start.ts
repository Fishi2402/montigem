/* (c) https://github.com/MontiCore/monticore */

import {StartComponent} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {config} from "@src/gui/StartStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class Start extends StartComponent {

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      super.router.navigate(['gui/login/LoginPage']);
    }
  }

}


