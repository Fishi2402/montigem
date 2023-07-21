import { Component } from "@angular/core";

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [
    { url: '/a/WeatherView', title: 'Weather View' },
    { url: '/c/WeatherApp', title: 'Weather App' },
  ];

}
