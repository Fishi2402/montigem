import {Component} from '@angular/core';
import {NavList as NavListTOP} from "./nav-list/nav-list.component-gen";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: "nav-list",
  templateUrl: "./nav-list.component.html",
  styleUrls: ["./nav-list.component.scss"]
})
export class NavList extends NavListTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
  }

}
