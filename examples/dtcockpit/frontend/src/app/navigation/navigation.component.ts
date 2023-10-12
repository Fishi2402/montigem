import {Component} from "@angular/core";
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
import {User} from '@src/dtcockpit/User';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  public constructor(route: ActivatedRoute, protected router: Router) {
    this.router = router;
  }

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return DtcockpitSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      DtcockpitSecurityManager.signOut();
      this.router.navigate(['/login/LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn();
  }
}
