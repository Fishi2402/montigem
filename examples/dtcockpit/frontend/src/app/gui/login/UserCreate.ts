/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserCreateTOP} from '@src/gui/login/UserCreateTOP';
import {User} from '@src/dtcockpit/User';
import {DtcockpitRole} from '@src/dtcockpit/DtcockpitRole';

import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {BoardMapper} from '@src/dtcockpit/BoardMapper';
import {BingoEntry} from '@src/dtcockpit/BingoEntry';
import {GemList} from '@umlp/common';

@Directive()

export class UserCreate extends UserCreateTOP {

  private error: string = "";

  protected users: GemList<User>;

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  private permutation : number[];
  private entries : BingoEntry[];

  public override createUser(event: any): void {
    DtcockpitManager.getBingoEntryList().then(entryList => {
      entryList.loadAll().then(e => {
        this.entries = e;

        this.error = "";
        if (!this.getNewUser().getUsername() || !this.getNewUser().getPassword() || !this.getNewUser().getEmail()) {
          return;
        }
        let errorMessage = this.validateUser();
        if (!errorMessage) {
          this.permutation = this.getBoardPermutation();
          this.buildMapping().then(mapping => {
          //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
          this.newUserBuilder.registrationDate(new Date());
          this.newUserBuilder.role(DtcockpitRole.DEVELOPER);
          this.newUserBuilder.gridEntry(mapping);
          this.build();
          this.navigateBack();
          });
        } else {
          this.error = errorMessage;
        }
      });
    });
  }

  async buildMapping(): Promise<Set<BoardMapper>>  {
    let promises = [];
    let gridEntrySet = new Set<BoardMapper>()

    for (let i = 0; i <= 24; i++) {
      let promise = DtcockpitManager.boardMapperBuilder().then(builder => {
        console.log(this.permutation[i], this.permutation.at(i))
        console.log(this.entries[this.permutation[i]])
        gridEntrySet.add(
          builder
            .bingoBoardEntry(this.entries[this.permutation[i]])
            .isChecked(false)
            .build()
        );
      });

      promises.push(promise);
    }

    await Promise.all(promises);
    console.log(gridEntrySet)
    return gridEntrySet;
  }


  public getBoardPermutation(): number[] {
    const arr: number[] = Array.from({ length: 25 }, (_, i) => i);
    for (let i = arr.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [arr[i], arr[j]] = [arr[j], arr[i]]; // Swap elements
    }
    return arr;
  }

  public override getErrorValue(): string {
    return this.error;
  }

  private validateUser(): string {
    let res: string = '';
    // Check Email format
    if (!UserCreate.email.test(this.getNewUser().getEmail())) {
      return 'Wrong format for Email address!';
    }

    // Check if user is unique
    this.users.toArray().filter(user => {
      if (this.getNewUser().getUsername() === user.getUsername()) {
        res += ' Account "' + this.getNewUser().getUsername() + '"';
      }
      if (this.getNewUser().getEmail() === user.getEmail()) {
        res += (res.length === 0? '': ' und ') + ' E-mail "' + this.getNewUser().getEmail() + '"';
      }
    });
    return res + (res.length === 0? '': ' already being used!');
  }

  public override navigateBack(): void {
    this.router.navigate(['/login/LoginPage']);
  }

}
