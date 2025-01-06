/* (c) https://github.com/MontiCore/monticore */

import { GemTreeViewExampleComponent, config } from '@src/example_arrange/GemTreeViewExampleComponent';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class GemTreeNodeExample extends GemTreeViewExampleComponent {
  public override sayHello(name: string): (arg: void) => void {
    return () => alert("Hello " + name);
  }
}
