/* (c) https://github.com/MontiCore/monticore */

import {StartComponentTOP} from '@src/gui/StartComponentTOP';
import {Component} from '@angular/core';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {GetCurrentUserRequest} from "../security/GetCurrentUserRequest";
import {CommandManager} from '@umlp/common';

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

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get())
      }else{
        console.log("Can not get GemId of current user");
      }
    });
  }

}


