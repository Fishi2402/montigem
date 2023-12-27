/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponent} from '@src/gui/login/LoginPageComponent';

@Component({
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/LoginPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Login.scss"]
})

export class LoginPage extends LoginPageComponent {

}
