/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';

import { SubComponentViewComponent } from '../../../../build/frontendGen/build/appgui/gui/component/SubComponentViewComponent';

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
  selector: 'sub-component-view',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/component/SubComponentView.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class SubComponentView extends SubComponentViewComponent {

  public override getEditSubcomponentUrl (): string {
    return "/gui/component/ComponentEdit/"+this.getSubComponent().getGemId();
  }
}


