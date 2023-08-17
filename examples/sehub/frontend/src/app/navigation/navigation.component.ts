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
    { url: '/artefact-model', title: 'Artefaktmodell' },
    { url: '/notes', title: 'Notizen' }
  ];

}
