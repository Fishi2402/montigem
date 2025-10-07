/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemIconComponent } from '../../../../build/gui/mc/fenix/basic/GemIconComponent';
import { MatIconModule } from '@angular/material/icon';

@Component({
  standalone: true,
  imports: [...config.imports, MatIconModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemIcon extends GemIconComponent {
}