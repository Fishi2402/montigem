/* (c) https://github.com/MontiCore/monticore */

import { TableColumn } from '@umlp/table';
import { Component } from '@angular/core';
import { AccountOverviewRefComponent, config } from '@src/gui/refactor/AccountOverviewRefComponent';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class AccountOverviewRef extends AccountOverviewRefComponent {

  private formatter = new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2
  });

  public override init() {
    this.gemTable3Columns = <any[]>[
      { prop: "_name", name: "Name" },
      { prop: "_kontotyp", name: "Typ" },
      { prop: "_budgetRahmenCent", name: "Gesamtbudget", displayValue: v => this.formatter.format(v)  }
    ];
  }

}


