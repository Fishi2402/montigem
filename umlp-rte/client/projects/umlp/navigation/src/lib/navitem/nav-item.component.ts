import {Component} from '@angular/core';
import {NavItem as NavItemTOP} from "./nav-item/nav-item.component-gen";
import {ActivatedRoute, Router, Scroll} from "@angular/router";
import {filter} from 'rxjs/operators';
@Component({
  selector: "nav-item",
  templateUrl: "./nav-item.component.html",
  styleUrls: ["./nav-item.component.scss"]
})
export class NavItem extends NavItemTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super();

    router.events.pipe(
      filter(event => event instanceof Scroll)
    )
      .subscribe(event => {
        this.isActive = (event as Scroll).routerEvent.url.startsWith(this.target);
      });

  }

  protected isActive: boolean = false;

  public navigateOrAct(){
    if (this.target != null) {
      this.router.navigate([this.target]);
    } else {
      this.leftClick.emit();
    }
  }
}
