/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding, Input } from '@angular/core';
import { config, GemImageComponent } from '@src/mc/fenix/basic/GemImageComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemImage extends GemImageComponent {
  @HostBinding("style.width")
  @Input()
  _width: string = 'auto';

  @HostBinding("style.height")
  @Input()
  _height: string = 'auto';

}