/* (c) https://github.com/MontiCore/monticore */

import { Dimension } from '@src/demorec/Dimension';

import { DimensionCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/dimension/DimensionCreateComponent';

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

  public override createDimension() {
    this.getDimension().build();
    super.ngOnInit();
  }
}


