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
    { url: '/file-explorer', title: 'File Explorer' },
    {  url: '/issue-tracker/IssueTrackerPage', title: 'Issue Tracker' },
    { url: '/projects', title: 'Artifacts' },
    { url: '/notes/NotesPage/42000', title: 'Notes'},
    { url: '/usersettings/UserSettings/1', title: 'User Settings'},
    { url: '/CD2GUI', title: 'CD2GUI'},
    { url: '/login', title: 'Login' },
  ];
  getCurrentUser(): User {
    return SehubSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    SehubSecurityManager.signOut();
  }


}
