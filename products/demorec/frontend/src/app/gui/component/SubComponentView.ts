/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';

import { SubComponentViewComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/SubComponentViewComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class SubComponentView extends SubComponentViewComponent {

  public constructor(protected route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  public override editSubcomponent (): void {
    console.debug("Click: ", this.getSubComponent().getGemId())
    this.router.navigate(["/gui/component/ComponentEdit/"+this.getSubComponent().getGemId()])
  }
}


