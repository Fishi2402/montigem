/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemTableComponent } from '../../../../build/gui/mc/fenix/table/GemTableComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTable extends GemTableComponent {
  @HostBinding("style.width")
  _width: string;

  @HostBinding("style.height")
  _height: string;

  @HostBinding("style.overflow")
  overflow: string = "auto";

  override _layout: string = "auto";

  ngOnInit(): void { }
}