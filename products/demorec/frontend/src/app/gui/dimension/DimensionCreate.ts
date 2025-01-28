/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { DimensionCreateComponent, config } from 'gui/dimension/DimensionCreateComponent';

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
export class DimensionCreate extends DimensionCreateComponent {

  public override createDimension(): (arg: void) => void {
    return $ => {
      this.getDimension().build();

      DemoRecManager.dimensionBuilder().then(tmp_$ => {
        this._dimension = tmp_$
      });
    }
  }
}


