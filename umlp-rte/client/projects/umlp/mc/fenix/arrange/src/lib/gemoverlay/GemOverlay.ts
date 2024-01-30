/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, TemplateRef } from '@angular/core';

@Component({
  'selector': 'gem-overlay',
  'templateUrl': './GemOverlay.html',
  'styleUrls': ['./GemOverlay.scss']
})
export class GemOverlay {
  protected _background: TemplateRef<any>;
  protected _components: TemplateRef<any>[] = [];

  public get background(): TemplateRef<any> {
    return this._background;
  }

  @Input()
  public set background(background: TemplateRef<any>) {
    this._background = background;
  }

  public get components(): TemplateRef<any>[] {
    return this._components;
  }

  @Input()
  public set components(components: TemplateRef<any>[]) {
    this._components = components;
  }
}
