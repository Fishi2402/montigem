/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from 'domain/Person';
import { config, SimpleOverviewComponent } from 'person/SimpleOverviewComponent';
import { Event, previousIfUnchanged } from '@umlp/common';
import { Option } from '@umlp/input';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class SimpleOverview extends SimpleOverviewComponent {

  public override getPersonOptions(): Array<Option> {
    const opts: Option[] = [];
    if (this.getPersons()) {
      for (const p of this.getPersons()) {
        opts.push(new Option(p.getGemId() + '', p.getName()));
      }
    }
    return previousIfUnchanged(this.getPersonOptions, opts);
  }

  public override getPotentialFriends(): Array<Option> {
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

  public override addFriend(personId: number, friendId: number): Event<void> {
    return () => {
      const p = this.getPersons().find(p => p.getGemId() === personId);
      const f = this.getPersons().find(p => p.getGemId() === friendId);
      if (p && f) {
        p.addFriends2(f);
      }
    }
  }

}
