/* (c) https://github.com/MontiCore/monticore */

import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, Scroll } from "@angular/router";
import { config, GemNavItemComponent } from './build/gui/mc/fenix/navigation/GemNavItemComponent';
import { filter } from 'rxjs/operators';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styles: [
    ...config.styles,
    `
      :host {
        width: 100%;
      }
    `
  ],
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemNavItem extends GemNavItemComponent {

  public isActive: boolean = false;

  public constructor(
    protected route: ActivatedRoute,
    protected router: Router,
    protected cdRef: ChangeDetectorRef
  ) {
    super();
  }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof Scroll)
    ).subscribe(event => {
      if ((event as Scroll).routerEvent instanceof NavigationEnd) {
        this.isActive = ((event as Scroll).routerEvent as NavigationEnd).urlAfterRedirects === this.target;
      }

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