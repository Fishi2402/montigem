/* (c) https://github.com/MontiCore/monticore */

import { EditComponentKindComponent, config } from '@src/gui/kind/EditComponentKindComponent';
import {GemTextInputValidity} from '@umlp/input';

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
export class EditComponentKind extends EditComponentKindComponent {

  public override checkKindName(): GemTextInputValidity {
    if(this.getComponentKind()) {
      if(this.getComponentKind().getName()) {
        return {status: "Success"}
      }
      return {status: "Error", message: "Enter a name."}
    }
    return {status: "Warning", message: "Wait for page init."}
  }

}


