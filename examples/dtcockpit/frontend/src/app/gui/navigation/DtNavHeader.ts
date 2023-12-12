/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { DtcockpitSecurityManager } from "@src/dtcockpit/DtcockpitSecurityManager";
import { User } from '@src/dtcockpit/User';
import { DtNavHeaderComponent } from '@src/gui/navigation/DtNavHeaderComponent';
import {config} from "@src/gui/navigation/DtNavHeaderStandaloneConfig";
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[...config.imports,RouterModule,CommonModule],
  selector: "dt-nav-header",
  templateUrl: "./DtNavHeader.html",
  styleUrls: ["./DtNavHeader.scss"]
})
export class DtNavHeader extends DtNavHeaderComponent {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
    this.router = router;
  }

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return DtcockpitSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      DtcockpitSecurityManager.signOut();
      this.router.navigate(['gui', 'login', 'LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn();
  }
}
