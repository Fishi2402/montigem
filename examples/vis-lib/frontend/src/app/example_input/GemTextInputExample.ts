/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from '@src/domain/Person';
import { GemTextInputExampleComponent, config } from '@src/example_input/GemTextInputExampleComponent';
import { Option } from '@umlp/input';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemTextInputExample extends GemTextInputExampleComponent {

  protected options: Option[] = [
    {value: "1", display: "John"},
    {value: "2", display: "Alice"},
    {value: "3", display: "Bob"},
  ];

  public override getDropdownOptions(p: Person): Option[] {
    return this.options
  }

  public override setDropdownEntry(value: string, p: Person): void {
    // own logic
  }

}
