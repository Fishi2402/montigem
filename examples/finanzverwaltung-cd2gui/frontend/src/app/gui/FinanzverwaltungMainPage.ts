/* (c) https://github.com/MontiCore/monticore */

import {FinanzverwaltungMainPageComponent,config} from '@src/gui/FinanzverwaltungMainPageComponent';
import {Component} from '@angular/core';
import {FinanzverwaltungSecurityManager} from "../finanzverwaltung/FinanzverwaltungSecurityManager";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class FinanzverwaltungMainPage extends FinanzverwaltungMainPageComponent {
  ngOnInit(){
    if (!FinanzverwaltungSecurityManager.isSignedIn()) {
      this._router.navigate(['gui', 'login', 'LoginPage']);
    }
    super.ngOnInit();
  }

}
