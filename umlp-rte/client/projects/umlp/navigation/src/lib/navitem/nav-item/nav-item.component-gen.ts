import {Component, Input} from '@angular/core';

@Component(
  {
    "selector": "nav-item",
    "templateUrl": "./nav-item.component-gen.html",
    "styles": [":host { display:flex; flex-direction:column; }"]
  })
export class NavItem {
  protected _title: string;
  protected _target: string;

  public get title(): string {
    return this._title;
  }

  @Input()
  public set title(title: string) {
    this._title = title;
  }

  public get target(): string {
    return this._target;
  }

  @Input()
  public set target(target: string) {
    this._target = target;
  }
}
