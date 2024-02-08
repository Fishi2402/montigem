/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';

import { ComponentEditComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentEditComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentEdit extends ComponentEditComponent {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router.events.subscribe((event) => {
      if(event instanceof NavigationEnd) {
        this.componentRoutingId = +route.snapshot.paramMap.get("componentRoutingId");
        this.ngOnInit()
      }
    })
  }
}


