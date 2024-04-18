/* (c) https://github.com/MontiCore/monticore */

import { GemTableExampleComponent,config } from '@src/example_table/GemTableExampleComponent';
import { Component } from '@angular/core';
import { TableColumn } from '@umlp/table';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
})
export class GemTableExample extends GemTableExampleComponent {
  override init(): void {
    /*
    this.gemTable0Columns = [];
    this.gemTable0Columns.push(new TableColumn("_name", "Name"));
    this.gemTable0Columns.push(new TableColumn("_birthday", "Birthday"));
    /**/
  }
}
