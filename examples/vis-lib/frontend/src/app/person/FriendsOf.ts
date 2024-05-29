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
      console.error(newFriend, this.getPerson())
      this.getPerson().addFriends2(newFriend)
    };
  }

}
