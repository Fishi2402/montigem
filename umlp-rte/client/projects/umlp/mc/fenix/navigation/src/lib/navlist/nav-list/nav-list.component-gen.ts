import {Component, HostBinding, Input, TemplateRef} from '@angular/core';

@Component(
  {
    "selector": "gem-nav-list",
    "templateUrl": "./nav-list.component-gen.html",
  })
export class NavList {
  @HostBinding("style.width")
  _width: string = "auto";

  @HostBinding("style.height")
  _height: string = "100%";

  public get width(): string {
    return this._width;
  }

  @Input()
  public set width(width: string) {
    this._width = width;
  }

  public get height(): string {
    return this._height;
  }

  @Input()
  public set height(height: string) {
    this._height = height;
  }

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
