/* (c) https://github.com/MontiCore/monticore */

import { TableColumn } from '@umlp/table';
import { Component } from '@angular/core';
import { PersonOverviewRefComponent, config } from '@src/gui/refactor/PersonOverviewRefComponent';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class PersonOverviewRef extends PersonOverviewRefComponent {

  public override init() {
    this.gemTable3Columns_0 = <any[]>[
      { prop: "_nachname", name: "Surname" },
      { prop: "_vorname", name: "Name" },
      {
        prop: "_beschBeginn", name: "Contract start", displayValue: v => new Date(v).toLocaleDateString("de-DE", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
        })
      },
      {
        prop: "_beschEnde", name: "Contract end", displayValue: v => new Date(v).toLocaleDateString("de-DE", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
        })
      }
    ];
  }

}


