/* (c) https://github.com/MontiCore/monticore */
import { DefineDependenciesComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/DefineDependenciesComponent';

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
export class DefineDependencies extends DefineDependenciesComponent {
  public override back() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineActions/" + this.processModel.getGemId()]);
  }

  public override next() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/GraphicalView/" + this.processModel.getGemId()]);
  }
}
