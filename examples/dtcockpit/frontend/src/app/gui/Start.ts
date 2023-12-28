/* (c) https://github.com/MontiCore/monticore */

import {StartComponent,config} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
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

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router = router;
  }

  ngOnInit(){
    if (!(DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn())) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
      return;
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
    super.ngOnInit();
  }

}


