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

  protected override gemGrid6X: Array<any> = ["200px", "1fr", "auto"];

  protected override gemGrid6Y: Array<any> = ["auto", "20px"];

}
