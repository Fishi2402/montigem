/* (c) https://github.com/MontiCore/monticore */

import { ClubMembersComponent, config } from '@src/person/ClubMembersComponent';
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
export class ClubMembers extends ClubMembersComponent {

  public override updateClubMembers(): Event<Person> {
    return newMember => this.getClub().addMembers2(newMember);
  }

}
