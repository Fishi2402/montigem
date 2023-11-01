import {Component, Input, TemplateRef} from '@angular/core';

@Component(
  {
    "selector": "nav-list",
    "templateUrl": "./nav-list.component-gen.html",
    "styles": [":host { display:flex; flex-direction:column; padding-left: 10px; }"]
  })
export class NavList {
  protected _components: TemplateRef<any>[];

  public get components(): TemplateRef<any>[] {
    return this._components;
  }

  @Input()
  public set components(components: TemplateRef<any>[]) {
    this._components = components;
  }
}
