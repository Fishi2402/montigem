/* (c) https://github.com/MontiCore/monticore */

import {StartComponent} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {GuidslpaperManager} from '@src/guidslpaper/GuidslpaperManager';
import {GuidslpaperSecurityManager} from "../guidslpaper/GuidslpaperSecurityManager";
import {GetCurrentUserRequest} from "../security/GetCurrentUserRequest";
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: row }"],
  styleUrls: []
})

export  class Start extends StartComponent {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router = router;
  }

  ngOnInit(){
    if (!(GuidslpaperSecurityManager.isCurrentlyLoggingIn() || GuidslpaperSecurityManager.isSignedIn())) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
      return;
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if(sr.isSuccessful()){
        console.log("GemId of current user is", sr.get());
        GuidslpaperManager.getUserSure(sr.get()).then(usr => {
          GuidslpaperSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      }else{
        console.error("Can not get GemId of current user");
      }
    });
    super.ngOnInit();
  }

}


