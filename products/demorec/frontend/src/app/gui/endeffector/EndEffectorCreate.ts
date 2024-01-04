/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';
import { EndEffector } from '@src/demorec/EndEffector';

import { EndEffectorCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/endeffector/EndEffectorCreateComponent';

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
export class EndEffectorCreate extends EndEffectorCreateComponent {

  public override createEndEffector() {
    this.getEndEffector().build();
    super.ngOnInit();
  }
}


