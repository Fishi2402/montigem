/* (c) https://github.com/MontiCore/monticore */

import { CreateComponent } from '@src/person/CreateComponent';
import { Component } from '@angular/core';
import { DomainManager } from '@src/domain/DomainManager';

@Component({
  selector: 'create',
  templateUrl: '../../../build/frontendGen/build/appgui/person/Create.html',
  styleUrls: ['./Create.scss']
})
export class Create extends CreateComponent {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override create(): void {
    const addr = this.address.build();
    this.person.address(addr).build();
    DomainManager.addressBuilder().then(b => {
      this.address = b;
    });
    DomainManager.personBuilder().then(b => {
      this.person = b;
    });
  }

}
