/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [

    { url: 'gui/SehubMainPage', title: 'Projects' },
    { url: 'gui/issues/IssueTrackerPage', title: 'Issue Tracker' },
    { url: 'gui/usersettings/UserSettings/1', title: 'User Settings' },
    { url: 'cd2gui/models/CD2GUIDashboard', title: 'CD2GUI'},
  ];
  getCurrentUser(): User {
    return SehubSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout")
    SehubSecurityManager.signOut();
  }
  isSignedIn(): boolean {
    return SehubSecurityManager.isSignedIn();
  }


}
