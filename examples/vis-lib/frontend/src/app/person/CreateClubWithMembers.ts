/* (c) https://github.com/MontiCore/monticore */

import { CreateClubWithMembersComponent, config } from '@src/person/CreateClubWithMembersComponent';
import { Component,  Output, EventEmitter } from '@angular/core';
import { Person } from '@src/domain/Person';
import { DomainManager } from '@src/domain/DomainManager';
import { Event } from '@umlp/common';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class CreateClubWithMembers extends CreateClubWithMembersComponent {

  public override createClub(): (arg: any) => void {
    return () => {
      this.getClub().build();
      this._router.navigate(['/person/ClubMembers/'+this.getClub().getGemId()]);
    }
  }

  public override updateClubMembers(): Event<Person> {
    return newMember => this.getClub().addMembers2(newMember);
  }

  public override createAlice() : Event<void> {
    return () => DomainManager.addressBuilder().then(addressBuilder => {
      const address = addressBuilder.street("Elmstreet").house(13).build()

      DomainManager.personBuilder().then(pBuilder => {
        pBuilder
            .name("Alice")
            .address(address)
            .birthday(new Date())
            .stocks(new Set())
            .aboutMe("");
        this.getClub().addMembers2(pBuilder);
      })
    });

  }

}
