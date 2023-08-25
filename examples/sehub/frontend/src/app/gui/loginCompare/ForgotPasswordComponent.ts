/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {GemList} from '@umlp/common';
import {User} from '@src/sehub/User';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';
import {ForgotPassword} from "./ForgotPassword";


@Component({
  selector: 'forgot-password',
  templateUrl: './ForgotPassword.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ForgotPasswordComponent extends ForgotPassword {

  @Input()
  public users: GemList<User>;
  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
  }

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });
  }

}


