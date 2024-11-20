/* (c) https://github.com/MontiCore/monticore */

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

  public override formatCurrency(value: number): string {
    return this.formatter.format(value);
  }

}


