/* (c) https://github.com/MontiCore/monticore */
import { DefineProcessModelComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/DefineProcessModelComponent';

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

export class DefineProcessModel extends DefineProcessModelComponent {
  public override getProcessStartedAt (): string {
    if (this.getProcessModel() !== null && this.getProcessModel() !== undefined) {
      if (this.getProcessModel().getUnderlyingProcess() !== null && this.getProcessModel().getUnderlyingProcess() !== undefined) {
        return this.getProcessModel().getUnderlyingProcess().getStartedAt().toLocaleDateString('de-DE');
      }
    }
    return "";
  }

  public override back() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/GraphicalView/" + this.getProcessModel().getGemId()]);
  }

  public override next() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineActions/" + this.getProcessModel().getGemId()]);
  }
}


