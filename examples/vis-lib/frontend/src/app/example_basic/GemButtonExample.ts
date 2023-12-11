/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemButtonExampleComponent } from '@src/example_basic/GemButtonExampleComponent';

@Component({
  selector: 'gem-button-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_basic/GemButtonExample.html'
})
export class GemButtonExample extends GemButtonExampleComponent {
  gemButton0LeftClick(event: MouseEvent): void {
    alert('Hello')
  }
}
