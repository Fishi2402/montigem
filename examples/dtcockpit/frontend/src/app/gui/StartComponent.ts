/* (c) https://github.com/MontiCore/monticore */

import {StartComponentTOP} from '@src/gui/StartComponentTOP';
import {Component} from '@angular/core';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
import {GetCurrentUserRequest} from "../security/GetCurrentUserRequest";
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class StartComponent extends StartComponentTOP {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
    this.router = router;
  }

  ngOnInit(){
    if (!(DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn())) {
      this.router.navigate(['/login/LoginPage']);
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get());
        DtcockpitManager.getUserSure(sr.get()).then(usr => {
          DtcockpitSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      }else{
        console.error("Can not get GemId of current user");
      }
    });
  }

}


