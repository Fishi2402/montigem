/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponent,config} from '@src/gui/login/LoginPageComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})

export class LoginPage extends LoginPageComponent {

  /*
  public ngOnInit(): void {
    DtcockpitManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }
  /**/

}
