/* (c) https://github.com/MontiCore/monticore */

import { CreateComponent,config } from '@src/person/CreateComponent';
import { Component } from '@angular/core';
import { DomainManager } from '@src/domain/DomainManager';
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class Create extends CreateComponent {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override create(): void {
    const addr = this.addressBuilder.build();
    this.personBuilder.address(addr).build();
    DomainManager.addressBuilder().then(b => {
      this.addressBuilder = b;
    });
    DomainManager.personBuilder().then(b => {
      this.personBuilder = b;
    });
  }

}
