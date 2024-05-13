/* (c) https://github.com/MontiCore/monticore */

import { GemGridExampleComponent, config } from '@src/example_arrange/GemGridExampleComponent';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class GemGridExample extends GemGridExampleComponent {

  protected override gemGrid12X: Array<any> = ["200px", "1fr", "auto"];

  protected override gemGrid12Y: Array<any> = ["auto", "20px"];

}
