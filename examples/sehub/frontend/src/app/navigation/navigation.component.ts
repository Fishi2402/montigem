/* (c) https://github.com/MontiCore/monticore */
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {User} from 'sehub/User';
import {ActivatedRoute, Router} from '@angular/router';
import { Component } from "@angular/core";
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
      this._router.navigate(['gui/login/LoginPage']);
    });
  }
  isSignedIn(): boolean {
    return SehubSecurityManager.isCurrentlyLoggingIn() || SehubSecurityManager.isSignedIn();
  }
}
