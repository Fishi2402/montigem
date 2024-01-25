/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemButtonExampleComponent,config } from '@src/example_basic/GemButtonExampleComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemButtonExample extends GemButtonExampleComponent {
  gemButton0LeftClick(event: MouseEvent): void {
    alert('Hello')
  }
}
