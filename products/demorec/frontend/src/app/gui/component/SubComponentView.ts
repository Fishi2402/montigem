/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { BatteryPackType } from 'demorec/BatteryPackType';

import { BatteryPack } from 'demorec/BatteryPack';
import { JoinConnection } from 'demorec/JoinConnection';

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

  public override editSubcomponent(): (arg: void) => void {
    return $ => {
      console.debug("Click: ", this.getSubComponent().getGemId())
      this._router.navigate(["/gui/component/ComponentEdit/"+this.getSubComponent().getGemId()])
    };
  }

}


