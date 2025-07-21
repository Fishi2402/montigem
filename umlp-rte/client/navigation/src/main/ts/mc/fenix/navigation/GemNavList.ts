/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemNavListComponent } from '../../../../build/gui/mc/fenix/navigation/GemNavListComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styles: [
    `
      :host {
        width: 100%;
        display: flex;
        position: sticky;
        overflow-y: auto;
        top: 0;
        left: 0;
      }
    `
  ],
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemNavList extends GemNavListComponent {
  @HostBinding("style.width")
  _width: string = "auto";

  @HostBinding("style.height")
  _height: string = "100%";
}