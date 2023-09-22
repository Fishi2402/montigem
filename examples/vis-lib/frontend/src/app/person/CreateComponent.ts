/* (c) https://github.com/MontiCore/monticore */

import { CreateComponentTOP } from '@src/person/CreateComponentTOP';
import { Component } from '@angular/core';

@Component({
  selector: 'create',
  templateUrl: '../../../build/frontendGen/build/appgui/person/Create.html',
  styles: [":host{ display: flex; flex-direction: column; row-gap: 5px }"],
  styleUrls: []
})
export class CreateComponent extends CreateComponentTOP {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override create(): void {
    const addr = this.addressBuilder.build();
    this.personBuilder.address(addr).build();
  }

}
