/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { config, GemLinkComponent } from '../../../../build/gui/mc/fenix/basic/GemLinkComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: config.styles,
  templateUrl: config.templateUrl,
})
export class GemLink extends GemLinkComponent {
  ngOnInit() {}
}