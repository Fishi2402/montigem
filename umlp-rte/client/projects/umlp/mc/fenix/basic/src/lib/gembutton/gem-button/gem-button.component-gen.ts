/* (c) https://github.com/MontiCore/monticore */

import { HostBinding, Output, TemplateRef } from '@angular/core';
import { Component } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { Input } from '@angular/core';

@Component({
  "selector": "gem-button",
  "templateUrl": "../gem-button.component.html",
  "styles": [":host { display:flex; flex-direction:column; }"]
})
export class GemButton {
  @HostBinding("style.width")
  _width: string = "auto";

  @HostBinding("style.height")
  _height: string = "auto";

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

  @Output()
  public leftClick: EventEmitter<any> = new EventEmitter();

  protected _label: string;

  protected _classes: string[];

  protected _style: Object;

  protected _disabled: Boolean;

  public get label(): string {
    return this._label;
  }

  @Input()
  public set label(label: string) {
    this._label = label;
  }

  @Input()
  public content: TemplateRef<any>[];

  public get classes(): string[] {
    return this._classes;
  }

  @Input()
  public set classes(classes: string[]) {
    this._classes = classes;
  }

  public get style(): Object {
    return this._style;
  }

  @Input()
  public set style(style: Object) {
    this._style = style;
  }

  public get disabled(): Boolean {
    return this._disabled;
  }

  @Input()
  public set disabled(disabled: Boolean) {
    this._disabled = disabled;
  }

  public onLeftClick(event): void {
    this.leftClick.emit(event);
  }

}
