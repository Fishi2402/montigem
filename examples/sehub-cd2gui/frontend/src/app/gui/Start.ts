/* (c) https://github.com/MontiCore/monticore */

import {StartComponent,config} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class Start extends StartComponent {

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      super.router.navigate(['gui/login/LoginPage']);
    }
  }

}


