// HWC-P

import { Component, Input } from "@angular/core";

@Component({
  selector: "gem-navigation",
  templateUrl: "./gem-navigation.component.html",
  styleUrls: ["./gem-navigation.component.scss"]
})
export class GemNavigation {

  @Input()
  items: { url: string, title: string }[] = [];

}