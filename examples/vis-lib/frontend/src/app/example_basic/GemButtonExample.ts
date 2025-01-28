/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemButtonExampleComponent,config } from 'example_basic/GemButtonExampleComponent';
import { Event } from '@umlp/common';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemButtonExample extends GemButtonExampleComponent {
  public override alert(): Event<void> {
    return () => alert('Hello');
  }
}
