/* (c) https://github.com/MontiCore/monticore */
import { ComponentEditPageComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentEditPageComponent';

import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import {JoinConnection} from "../../demorec/JoinConnection";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentEditPage extends ComponentEditPageComponent {

  public override getConnectionNameValue (connection: JoinConnection): string {
    return connection.getNameAfterLoad()
  }

}
