/* (c) https://github.com/MontiCore/monticore */

import { TableColumn } from '@umlp/table';
import { Component } from '@angular/core';
import { AccountOverviewComponent, config } from '@src/gui/konto/AccountOverviewComponent';

@Component({
  standalone: true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles
})
export class AccountOverview extends AccountOverviewComponent {

  public override init() {
    this.gemTable11Columns = [
      new TableColumn("_pspElement", "PSP-Element"),
      new TableColumn("_name", "Name"),
      new TableColumn("_kontotyp", "Typ"),
      new TableColumn("_kontoart", "Projektart"),
      new TableColumn("_budgetRahmenCent", "Gesamtbudget")
    ];
  }

}


