/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding, Input } from '@angular/core';
import { config, GemTextComponent } from './build/gui/mc/fenix/basic/GemTextComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemText extends GemTextComponent {
  @HostBinding("style.font-style")
  @Input()
  public _fontStyle: string = 'unset';

  @HostBinding("style.width")
  @Input()
  _width: string = 'auto';

  @HostBinding("style.height")
  @Input()
  _height: string = 'auto';

  @HostBinding("style.font-weight")
  @Input()
  _weight: string = 'unset';

  @HostBinding("style.font-family")
  @Input()
  _fontFamily: string = 'unset';

  @HostBinding("style.font-size")
  @Input()
  _size: string;

  @HostBinding("style.color")
  @Input()
  _color: string;
}