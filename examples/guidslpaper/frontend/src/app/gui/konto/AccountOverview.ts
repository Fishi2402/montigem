/* (c) https://github.com/MontiCore/monticore */

import { TableColumn } from '@umlp/table';
import { Component } from '@angular/core';
import { AccountOverviewComponent, config } from '@src/gui/konto/AccountOverviewComponent';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class AccountOverview extends AccountOverviewComponent {

  private formatter = new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2
  });

  public override init() {
    this.gemTable11Columns = <any[]>[
      { prop: "_pspElement", name: "PSP-Element" },
      { prop: "_name", name: "Name" },
      { prop: "_kontotyp", name: "Typ" },
      { prop: "_kontoart", name: "Projektart" },
      { prop: "_budgetRahmenCent", name: "Gesamtbudget", displayValue: v => this.formatter.format(v)  }
    ];
  }

}


