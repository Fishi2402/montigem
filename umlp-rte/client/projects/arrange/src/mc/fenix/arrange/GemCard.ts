/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemCardComponent } from '../../../../build/gui/mc/fenix/arrange/GemCardComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: [...config.styles, `:host { padding:2px; }`],
  templateUrl: config.templateUrl,
})
export class GemCard extends GemCardComponent {
  @HostBinding("style.width")
  _width: string = 'auto';

  @HostBinding("style.height")
  _height: string = 'auto';

  public ngOnInit(): void { }
}