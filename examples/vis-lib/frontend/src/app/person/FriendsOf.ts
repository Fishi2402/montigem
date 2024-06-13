/* (c) https://github.com/MontiCore/monticore */

import { FriendsOfComponent, config } from '@src/person/FriendsOfComponent';
import { Person } from '@src/domain/Person';
import { Component } from '@angular/core';
import { DomainManager } from '@src/domain/DomainManager';
import { Event } from '@umlp/common';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class FriendsOf extends FriendsOfComponent {

  public override updateFriendsList(): Event<Person> {
    return newFriend => {
      this.getPerson().addFriends2(newFriend)
    };
  }

  public override navigateNext(): Event<void> {
    return $ => {
      if (this.getPersons() != null) {
        for (let i = 0; i < this.getPersons().length; ++i) {
          const p = this.getPersons()[i];
          if (p.getGemId() === this.personRoutingId) {
            let id: number;
            if (i + 1 < this.getPersons().length) {
              id = this.getPersons()[i + 1].getGemId();
            }
            else {
              id = this.getPersons()[0].getGemId();
            }
            this._router.navigate(['person', 'FriendsOf', id]);
          }
        }
      }
    }
  }

}
