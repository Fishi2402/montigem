/* (c) https://github.com/MontiCore/monticore */

import { CreatePersonComponent, config } from '@src/person/CreatePersonComponent';
import { Component } from '@angular/core';
import { DomainManager } from '@src/domain/DomainManager';

function Mix<T extends new (...args: any[]) => {}>(base: T) {
  return class extends base {
    foo() {
      alert('here');
    }
  }
}

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class CreatePerson extends Mix(CreatePersonComponent) {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override create(): (arg: any) => void {
    return () => {
      this.foo();
      this.getPerson().setAddress1(this.getAddress());
      this.getPerson().setAboutMe1("none");
      // this.person.setStocks1(new Set());
      DomainManager.addPerson(this.getPerson().build());
    }
  }

  foo() {
    alert('Created person')
  }

}
