/* (c) https://github.com/MontiCore/monticore */

import { FriendsOfComponent, config } from 'person/FriendsOfComponent';
import { Person } from 'domain/Person';
import { Component } from '@angular/core';
import { DomainManager } from 'domain/DomainManager';
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

}
