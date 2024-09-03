/* (c) https://github.com/MontiCore/monticore */

import {StartComponent,config} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
import {GetCurrentUserRequest} from '@src/dtcockpit/commands/GetCurrentUserRequest';
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginSecManager} from "@src/loginmodule/LoginSecManager";

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
    // TODO: Move to the login module
    if (LoginSecManager.shouldAbortOnInit(this._router, this._route)) {
      return;
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get());
        DtcockpitManager.getUserSure(sr.get()).then(usr => {
          console.log("Username: " + usr.getUsername());
          DtcockpitSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      }else{
        console.error("Can not get GemId of current user");
      }
    });
    super.ngOnInit();
  }

}


