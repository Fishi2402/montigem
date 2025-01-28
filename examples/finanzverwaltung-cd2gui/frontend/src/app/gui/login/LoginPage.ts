/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponent,config} from 'gui/login/LoginPageComponent';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})


export class LoginPage extends LoginPageComponent {

}
