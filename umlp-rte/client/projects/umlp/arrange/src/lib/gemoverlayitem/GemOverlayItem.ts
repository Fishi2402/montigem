/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding, Input, TemplateRef } from '@angular/core';

@Component({
  'selector': 'gem-overlay-item',
  'templateUrl': './GemOverlayItem.html',
  'styleUrls': ['./GemOverlayItem.scss']
})
export class GemOverlayItem {
  protected _component: TemplateRef<any>;

  @HostBinding('style.left')
  protected _x: string = '0';

  @HostBinding('style.top')
  protected _y: string = '0';

  public get component(): TemplateRef<any> {
    return this._component;
  }

  @Input()
  public set component(value: TemplateRef<any>) {
    this._component = value;
  }

  public get x(): string {
    return this._x;
  }

  @Input()
  public set x(value: string) {
    this._x = value;
  }

  public get y(): string {
    return this._y;
  }

  @Input()
  public set y(value: string) {
    this._y = value;
  }

}
