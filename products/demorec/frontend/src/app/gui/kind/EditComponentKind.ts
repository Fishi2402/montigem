/* (c) https://github.com/MontiCore/monticore */

import { EditComponentKindComponent, config } from 'gui/kind/EditComponentKindComponent';
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
        return new GemTextInputValidity("Success")
      }
      return new GemTextInputValidity("Error", "Enter a name.");
    }
    return new GemTextInputValidity("Warning", "Wait for page init.");
  }

}


