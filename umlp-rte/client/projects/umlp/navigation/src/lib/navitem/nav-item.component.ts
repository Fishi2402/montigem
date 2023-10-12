import {Component} from '@angular/core';
import {NavItem as NavItemTOP} from "./nav-item/nav-item.component-gen";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: "nav-item",
  templateUrl: "./nav-item.component.html",
  styleUrls: ["./nav-item.component.scss"]
})
export class NavItem extends NavItemTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
  }

  public navigate(){
    this.router.navigate([this.target])
  }
}
