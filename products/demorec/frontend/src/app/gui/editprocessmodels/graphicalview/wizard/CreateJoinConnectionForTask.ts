/* (c) https://github.com/MontiCore/monticore */
import { JoinConnection } from 'demorec/JoinConnection';

import { CreateJoinConnectionForTaskComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/CreateJoinConnectionForTaskComponent';

import {
  Component,
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
export class CreateJoinConnectionForTask extends CreateJoinConnectionForTaskComponent {

  public override addNewJoinConnection(): (arg: JoinConnection) => void {
    return (connection: JoinConnection) => this.getTask().setDeconstructs1(connection);
  }

  public override backToDefineActions (): Event<void> {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineActions/" + this.getTask().getProcessModel().getGemId()]);
  }
}
