import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router, Scroll } from "@angular/router";
import { filter } from 'rxjs/operators';
import { NavItem as NavItemTOP } from "./nav-item/nav-item.component-gen";

@Component({
  selector: "nav-item",
  templateUrl: "./nav-item.component.html",
  styleUrls: ["./nav-item.component.scss"]
})
export class NavItem extends NavItemTOP {

  public isActive: boolean = false;

  public constructor(protected route: ActivatedRoute, protected router: Router, protected cdRef: ChangeDetectorRef) {
    super();
  }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof Scroll)
    ).subscribe(event => {
      if ("/gui/pipelines/PipelineGroupStats/103" === this.target) {
        console.error(this.target, (event as Scroll).routerEvent.urlAfterRedirects)
      }
      this.isActive = (event as Scroll).routerEvent.urlAfterRedirects === this.target;
    });
  }

  public navigateOrAct() {
    if (this.target != null) {
      this.router.navigate([this.target]);
    } else {
      this.leftClick.emit();
    }
  }
}
