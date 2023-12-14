import {Component, Input, TemplateRef} from '@angular/core';

@Component(
  {
    "selector": "nav-list",
    "templateUrl": "./nav-list.component-gen.html",
    "styles": [":host { display:flex; flex-direction:column; padding-left: 10px; }"]
  })
export class NavList {
  protected _components: TemplateRef<any>[];
  protected _header?: TemplateRef<any>;
  protected _footer?: TemplateRef<any>;

  public get components(): TemplateRef<any>[] {
    return this._components;
  }

  @Input()
  public set components(components: TemplateRef<any>[]) {
    this._components = components;
  }

  public get header(): TemplateRef<any> {
    return this._header;
  }

  @Input()
  public set header(header: TemplateRef<any>) {
    this._header = header;
  }

  public get footer(): TemplateRef<any> {
    return this._footer;
  }

  @Input()
  public set footer(footer: TemplateRef<any>) {
    this._footer = footer;
  }
}
