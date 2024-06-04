/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import {SehubManager} from '@src/sehub/SehubManager';
import {BoardMapper} from '@src/sehub/BoardMapper';
import {BingoEntry} from '@src/sehub/BingoEntry';
import {GemList} from '@umlp/common';
import {UserCreateComponent,config} from '@src/gui/login/UserCreateComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})
export class UserCreate extends UserCreateComponent {

  private error: string = "";

  protected users: GemList<User>;

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  private permutation : number[];
  private entries : BingoEntry[];

  public constructor(protected location: Location) {
    super();
  }

  public override init(): void {
    super.init();
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });
  }

  public override createUser(): (arg: void) => void {
    return $ => SehubManager.getBingoEntryList().then(entryList => {
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
          //this.user.activated(ActivationStatus.AKTIVIERT);
          this.getNewUser().setRegistrationDate(new Date());
          this.getNewUser().setRole(SehubRole.DEVELOPER);
          // this.getNewUser().setGridEntry1(mapping);
          this.getNewUser().build();
          this.router.navigate(['/Login']);
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
      let promise = SehubManager.boardMapperBuilder().then(builder => {
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

  public override navigateBack(): (arg: void) => void {
    return $ => this.location.back();
  }

}
