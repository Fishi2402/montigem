/* (c) https://github.com/MontiCore/monticore */

import { CreateComponent, config } from '@src/person/CreateComponent';
import { Component,  Output, EventEmitter } from '@angular/core';
import { Person } from '@src/domain/Person';
import { DomainManager } from '@src/domain/DomainManager';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class Create extends CreateComponent {

  @Output()
  public newPerson: EventEmitter<Person> = new EventEmitter<Person>()

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override createPerson(): (arg: any) => void {
    return () => {
      this.person.address(this.address.build());
      this.person.stocks(new Set());
      this.person.aboutMe("");
      this.newPerson.emit(this.person.build())
    }
  }

}
