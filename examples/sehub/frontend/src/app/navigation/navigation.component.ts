import { Component } from "@angular/core";

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [
    { url: '/login', title: 'Login' },
    { url: '/start', title: 'Start' }
  ];

}
