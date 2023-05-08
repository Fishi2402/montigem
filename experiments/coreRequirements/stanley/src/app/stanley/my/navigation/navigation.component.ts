// GEN-P: translated from Java

import { Component } from "@angular/core";
import { PersonCreate } from "../../personcreate/person-create/person-create.component";
import { PersonOverview } from "../../personoverview/person-overview/person-overview.component";

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: {url: string, title: string}[] = [
    { url: PersonCreate.url, title: PersonCreate.title },
    { url: PersonOverview.url, title: PersonOverview.title }
  ];

}