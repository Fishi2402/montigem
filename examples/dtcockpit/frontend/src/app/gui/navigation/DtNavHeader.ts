/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { DtcockpitSecurityManager } from "@src/dtcockpit/DtcockpitSecurityManager";
import { User } from '@src/dtcockpit/User';
import { DtNavHeaderComponent ,config} from '@src/gui/navigation/DtNavHeaderComponent';

import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[...config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class DtNavHeader extends DtNavHeaderComponent {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(router);
    this._router = router;
  }

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return DtcockpitSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      DtcockpitSecurityManager.signOut();
      this._router.navigate(['gui', 'login', 'LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn();
  }
}
