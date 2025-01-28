/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { AccountOverviewRefComponent, config } from 'gui/refactor/AccountOverviewRefComponent';

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

  public override formatCurrency(value: number): string {
    return this.formatter.format(value);
  }
}


