import {Component} from '@angular/core';
import {NavList as NavListTOP} from "./nav-list/nav-list.component-gen";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: "gem-nav-list",
  templateUrl: "./nav-list.component.html",
  styles: [`
    :host {
      width: 100%;
      display: flex;
      position: sticky;
      overflow-y: auto;
      top: 0;
      left: 0;
    }
  `]
})
export class GemNavList extends NavListTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
  }

}
