/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponent,config} from '@src/gui/login/LoginPageComponent';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})

export class LoginPage extends LoginPageComponent {

}
