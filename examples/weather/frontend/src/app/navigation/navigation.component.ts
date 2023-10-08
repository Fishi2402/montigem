import { Component } from "@angular/core";

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [
    { url: '/gui/WeatherView', title: 'Weather View' },
    { url: '/gui/WeatherApp', title: 'Weather App' },
  ];

}
