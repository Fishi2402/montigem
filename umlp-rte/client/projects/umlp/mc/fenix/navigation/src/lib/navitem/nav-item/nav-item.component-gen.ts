import {Component, EventEmitter, Input} from '@angular/core';

@Component(
  {
    "selector": "nav-item",
    "templateUrl": "./nav-item.component-gen.html",
    "styles": [":host { display:flex; flex-direction:column; }"]
  })
export class NavItem {
  protected _title: string;
  protected _target: string | null;
  protected _leftClick: EventEmitter<any> = new EventEmitter();

  public get title(): string {
    return this._title;
  }

  @Input()
  public set title(title: string) {
    this._title = title;
  }

  public get target(): string | null {
    return this._target;
  }

  @Input()
  public set target(target: string | null) {
    this._target = target;
  }

  public get leftClick(): EventEmitter<any> {
    return this._leftClick;
  }

  @Input()
  public set leftClick(leftClick: EventEmitter<any>) {
    this._leftClick = leftClick;
  }
}
