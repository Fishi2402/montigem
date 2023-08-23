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
    { url: '/issue-tracker', title: 'Issue Tracker' },
    { url: '/projects', title: 'Artifacts' },
    { url: '/notes/NotesPage/42000', title: 'Notes'},
    { url: '/usersettings/UserSettings/1', title: 'User Settings'},
    { url: '/login', title: 'Login' },
  ];
  getUser(): User {
    return SehubSecurityManager.getCurrentUser();
  }

  signOut(): void {
    SehubSecurityManager.signOut();
  }


}
