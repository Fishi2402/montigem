import { Component } from "@angular/core";

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [
    { url: '/file-explorer', title: 'Dateiansicht' },
    { url: '/issue-tracker', title: 'Issue Tracker' },
    { url: '/projects', title: 'Artefakte' },
    { url: '/notes/NotesPage', title: 'Notizen'},
    { url: '/usersettings/UserSettings/1', title: 'User Settings'},
    { url: '/login', title: 'Login' }
  ];

}
