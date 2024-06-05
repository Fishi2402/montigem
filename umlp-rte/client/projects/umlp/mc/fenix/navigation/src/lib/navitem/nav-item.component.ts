import { ChangeDetectorRef, Component } from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router, Scroll} from "@angular/router";
import { filter } from 'rxjs/operators';
import { NavItem as NavItemTOP } from "./nav-item/nav-item.component-gen";

@Component({
  selector: "gem-nav-item",
  templateUrl: "./nav-item.component.html",
  styles: [`
    :host {
      width: 100%;
      display: flex;
    }
  `]
})
export class GemNavItem extends NavItemTOP {

  public isActive: boolean = false;

  public constructor(protected route: ActivatedRoute, protected router: Router, protected cdRef: ChangeDetectorRef) {
    super();
  }

  ngOnInit() {
    this._router.events.pipe(
      filter(event => event instanceof Scroll)
    ).subscribe(event  => {
      if((event as Scroll).routerEvent instanceof NavigationEnd) {
        this.isActive = ( (event as Scroll).routerEvent as NavigationEnd).urlAfterRedirects === this.target;
      }

    });
  }

  public navigateOrAct() {
    if (this.target != null) {
      this._router.navigate([this.target]);
    } else {
      this.leftClick.emit();
    }
  }
}
