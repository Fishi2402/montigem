/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person_subscribeManagerEvents } from '@src/domain/commands/Person_subscribeManagerEvents';
import { Person } from '@src/domain/Person';
import { config, SimpleOverviewComponent } from '@src/person/SimpleOverviewComponent';
import { CommandManager, previousIfUnchanged } from '@umlp/common';
import { Option } from '@umlp/input';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class SimpleOverview extends SimpleOverviewComponent {

  public init(): void {
    CommandManager.executeCommand(new Person_subscribeManagerEvents());
    super.init();
  }

  public getPersonOptions(): Array<Option> {
    const opts: Option[] = [];
    if (this.getPersons()) {
      for (const p of this.getPersons()) {
        opts.push(new Option(p.getGemId() + '', p.getName()));
      }
    }
    return previousIfUnchanged(this.getPersonOptions, opts);
  }

  public getPotentialFriends(): Array<Option> {
    const opts: Option[] = [];
    let person: Person;
    for (const p of this.getPersons() ?? []) {
      if (this.getPersonId() === p.getGemId()) {
        person = p;
        break;
      }
    }
    for (const p of this.getPersons() ?? []) {
      let isPotentialFriend = true;
      for (const f of person?.getFriendsList() ?? []) {
        if (f.getGemId() === p.getGemId() || person.getGemId() === p.getGemId()) {
          isPotentialFriend = false;
          break;
        }
      }
      if (isPotentialFriend) {
        opts.push(new Option(p.getGemId() + '', p.getName()));
      }
    }
    return previousIfUnchanged(this.getPotentialFriends, opts);
  }

}
