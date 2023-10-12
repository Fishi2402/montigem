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

  readonly items: { url: string, title: string }[] = [
    { url: '/projects', title: 'Projects' },
    { url: '/issue-tracker/IssueTrackerPage', title: 'Issue Tracker' },
    { url: '/usersettings/UserSettings/1', title: 'User Settings' },
  ];
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
