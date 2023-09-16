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
    { url: '/projects', title: 'Projects' },
    {  url: '/issue-tracker/IssueTrackerPage', title: 'Issue Tracker' },
    { url: '/notes/NotesPage/42000', title: 'Notes'},
    { url: '/usersettings/UserSettings/1', title: 'User Settings'},
    { url: '/CD2GUI', title: 'CD2GUI'},
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
