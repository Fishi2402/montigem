/* (c) https://github.com/MontiCore/monticore */
import { ProcessModel } from 'demorec/ProcessModel'

import { ProcessModelSelectorComponent, config} from 'gui/editprocessmodels/sidebar/ProcessModelSelectorComponent';

import {
  Component
} from '@angular/core';
import {Event} from "@umlp/common";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class ProcessModelSelector extends ProcessModelSelectorComponent {
  public override navigateToPMView (pm: ProcessModel): Event<void> {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/GraphicalView/" + pm.getGemId()]);
  }

  public override createNewProcessModel(): Event<void> {
    return $ => this._router.navigate(["gui/editprocessmodels/create/CreateNewProcessModel"]);
  }
}
