/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ComponentKind } from '@src/demorec/ComponentKind';
import { Option } from '@umlp/input';

import { CreateComponentKindComponent, config } from '../../../../build/frontendGen/build/appgui/gui/kind/CreateComponentKindComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef
} from '@angular/core';

import { Router } from '@angular/router';
import {ComponentEdit} from "../component/ComponentEdit";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class CreateComponentKind extends CreateComponentKindComponent {

  @Output()
  public newKind : EventEmitter<ComponentKind> = new EventEmitter<ComponentKind>()

  public constructor(protected router: Router) {
    super(router);
  }

  public override createNewComponentKind() {
    if(this.getKind().isBuildable()) {
      this.newKind.emit(this.getKind().build())
    }
  }

}


