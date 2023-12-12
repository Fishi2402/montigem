/* (c) https://github.com/MontiCore/monticore */

import { GemTableExampleComponent } from '@src/example_table/GemTableExampleComponent';
import { Component } from '@angular/core';
import { TableColumn } from '@umlp/table';

@Component({
  selector: 'gem-table-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_table/GemTableExample.html',
  styles: [":host{ display: flex; flex-direction: column }"],
})
export class GemTableExample extends GemTableExampleComponent {
  override init(): void {
    this.gemTable0Columns = [];
    this.gemTable0Columns.push(new TableColumn("_name", "Name"));
    this.gemTable0Columns.push(new TableColumn("_birthday", "Birthday"));
  }
}
