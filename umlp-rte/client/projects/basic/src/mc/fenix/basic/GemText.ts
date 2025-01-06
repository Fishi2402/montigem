/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding, Input } from '@angular/core';
import { config, GemTextComponent } from '../../../../build/gui/mc/fenix/basic/GemTextComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemText extends GemTextComponent {
  @HostBinding("style.font-style")
  _fontStyle: string = 'unset';

  @HostBinding("style.width")
  _width: string = 'auto';

  @HostBinding("style.height")
  _height: string = 'auto';

  @HostBinding("style.font-weight")
  _weight: string = 'unset';

  @HostBinding("style.font-family")
  _fontFamily: string = 'unset';

  @HostBinding("style.font-size")
  _size: string;

  @HostBinding("style.color")
  _color: string;
}