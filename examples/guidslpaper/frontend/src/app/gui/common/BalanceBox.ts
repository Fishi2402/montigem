/* (c) https://github.com/MontiCore/monticore */

import { BalanceBoxComponent, config } from '@src/gui/common/BalanceBoxComponent';
import { Component, Input, HostBinding } from '@angular/core';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class BalanceBox extends BalanceBoxComponent {

  private formatter = new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2
  });

  @Input()
  public override color: string = "blue";

  @HostBinding("class.green-box")
  public get green() {
    return this.color === "green";
  }

  @HostBinding("class.blue-box")
  public get blue() {
    return this.color === "blue";
  }

  public getGemText1Value(): string {
    return this.formatter.format(this.getValue() / 100);
  }
}
