/* (c) https://github.com/MontiCore/monticore */

import {StartComponent,config} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {GetCurrentUserRequest} from "../security/GetCurrentUserRequest";
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';
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
    if (!(SehubSecurityManager.isCurrentlyLoggingIn() || SehubSecurityManager.isSignedIn())) {
      this._router.navigate(['gui/login/LoginPage']);
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get());
        SehubManager.getUserSure(sr.get()).then(usr => {
          SehubSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      }else{
        console.error("Can not get GemId of current user");
      }
    });
  }

}


