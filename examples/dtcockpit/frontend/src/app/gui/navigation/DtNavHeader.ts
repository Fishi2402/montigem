/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { DtcockpitSecurityManager } from "@src/dtcockpit/DtcockpitSecurityManager";
import { User } from '@src/dtcockpit/User';
import { DtNavHeaderComponent ,config} from '@src/gui/navigation/DtNavHeaderComponent';

import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
import {LoginSecManager} from "@src/dtcockpit/login/LoginSecManager";
@Component({
  standalone:true,
  imports:[...config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class DtNavHeader extends DtNavHeaderComponent {

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return LoginSecManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    DtcockpitSecurityManager.signOut(this._router)
  }

  isSignedIn(): boolean {
    return DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn();
  }
}
