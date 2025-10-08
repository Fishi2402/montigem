/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemButtonComponent } from '../../../../../../build/gui2java2ts/mc/fenix/basic/GemButtonComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: config.styles,
  templateUrl: config.templateUrl,
})
export class GemButton extends GemButtonComponent {
  @HostBinding("style.width")
  _width: string = "auto";

  @HostBinding("style.height")
  _height: string = "auto";

  public ngOnInit(): void { }
}