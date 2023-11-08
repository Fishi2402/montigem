import {Component} from "@angular/core";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
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
    { url: 'gui/SehubMainPage', title: 'Projects' },
    { url: 'gui/issues/IssueTrackerPage', title: 'Issue Tracker' },
    { url: 'gui/usersettings/UserSettings/1', title: 'User Settings' },
  ];
  getCurrentUser(): User {
    return SehubSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      SehubSecurityManager.signOut();
      this.router.navigate(['gui/login/LoginPage']);
    });
  }
  isSignedIn(): boolean {
    return SehubSecurityManager.isCurrentlyLoggingIn() || SehubSecurityManager.isSignedIn();
  }
}
