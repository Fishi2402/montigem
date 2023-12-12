/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemButtonExampleComponent } from '@src/example_basic/GemButtonExampleComponent';
import {config} from "@src/example_basic/GemButtonExampleStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'gem-button-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_basic/GemButtonExample.html'
})
export class GemButtonExample extends GemButtonExampleComponent {
  gemButton0LeftClick(event: MouseEvent): void {
    alert('Hello')
  }
}
