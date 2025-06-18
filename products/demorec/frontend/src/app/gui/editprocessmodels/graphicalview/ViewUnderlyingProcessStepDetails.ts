/* (c) https://github.com/MontiCore/monticore */
import { ViewUnderlyingProcessStepDetailsComponent,config} from 'gui/editprocessmodels/graphicalview/ViewUnderlyingProcessStepDetailsComponent';

import {
  Component,
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class ViewUnderlyingProcessStepDetails extends ViewUnderlyingProcessStepDetailsComponent {
  public override goBackToProcessModelView() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/GraphicalView/" + this.getTask().getProcessModel().getGemId()]);
  }
}


