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

  /*
  protected override fixedGridX: Array<string> = ["200px", "1fr", "auto"];

  protected override fixedGridY: Array<string> = ["auto", "20px"];

  protected override spanningGridX: Array<string> = ["1fr", "1fr", "1fr", "1fr"];
  protected override spanningGridY: Array<string> = ["1fr", "1fr", "1fr", "1fr"];

  protected override cell1A: Array<number> = [1, 1];
  protected override cell1B: Array<number> = [3, 2];

  protected override cell2A: Array<number> = [2, 3];

  protected override cell3A: Array<number> = [2, 2];
  protected override cell3B: Array<number> = [4, 4];
  /**/

}
