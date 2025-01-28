import {Component} from "@angular/core";
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
import {User} from 'dtcockpit/User';
import {ActivatedRoute, Router} from '@angular/router';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[RouterModule,CommonModule],
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  public constructor(protected _router: Router) {
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
