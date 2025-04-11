/* (c) https://github.com/MontiCore/monticore */

import {StartComponent,config} from 'gui/StartComponent';
import {Component} from '@angular/core';
import {FinanzverwaltungManager} from 'finanzverwaltung/FinanzverwaltungManager';
import {FinanzverwaltungSecurityManager} from "../finanzverwaltung/FinanzverwaltungSecurityManager";
import {GetCurrentUserRequest} from "../security/GetCurrentUserRequest";
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export  class Start extends StartComponent {

  ngOnInit(){
    if (!(FinanzverwaltungSecurityManager.isCurrentlyLoggingIn() || FinanzverwaltungSecurityManager.isSignedIn())) {
      this._router.navigate(['gui', 'login', 'LoginPage']);
      return;
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get());
        FinanzverwaltungManager.getUserSure(sr.get()).then(usr => {
          FinanzverwaltungSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      }else{
        console.error("Can not get GemId of current user");
      }
    });
    super.ngOnInit();
  }

}


