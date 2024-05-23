/* (c) https://github.com/MontiCore/monticore */

import { FriendsOfComponent, config } from '@src/person/FriendsOfComponent';
import { Person } from '@src/domain/Person';
import { Component } from '@angular/core';
import { DomainManager } from '@src/domain/DomainManager';
@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class FriendsOf extends FriendsOfComponent {

  public override updateFriendsList(newFriend: Person): void {
    console.warn("Add new Friend", newFriend.getGemId(), newFriend.getName())
    this.getPerson().addFriends2(newFriend)
  }

}
