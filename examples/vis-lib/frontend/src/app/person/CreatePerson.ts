/* (c) https://github.com/MontiCore/monticore */

import { CreatePersonComponent, config } from 'person/CreatePersonComponent';
import { Component } from '@angular/core';
import { CommandManager, Event } from '@umlp/common';
import { IsOld } from 'domain/commands/IsOld';
import { DomainManager } from 'domain/DomainManager';
import { Person } from 'domain/Person';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class CreatePerson extends CreatePersonComponent {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override createButtonLeftClick(): void {
    // TODO should this happen inside the person build call?
    this.getAddress().build()
    this.getPerson().setAddress1(this.getAddress());
    this.getPerson().setAboutMe("");
    super.createButtonLeftClick();

    DomainManager.personBuilder().then(p => this.setPerson(p));
    DomainManager.addressBuilder().then(a => this.setAddress(a));
    this.newPerson.emit(this.getPerson());
  }

  public override doCancel(): Event<void> {
    return () => this.cancel.emit(this.getPerson());
  }

  foo() {
    alert('Created person')
  }

}
