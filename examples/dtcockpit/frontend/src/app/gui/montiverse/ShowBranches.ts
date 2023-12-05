/* (c) https://github.com/MontiCore/monticore */
import {ShowBranchesComponent} from '@src/gui/montiverse/ShowBranchesComponent';
import { Branch } from '@src/dtcockpit/Branch';

import {
  Component
} from '@angular/core';

@Component({
  selector: 'show-branches',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/montiverse/ShowBranches.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ShowBranches extends ShowBranchesComponent {

  public override chooseBranch (g: Branch): (arg: any) => void {
    return event => {
      g.getProject().getCiInfos().setBranchName(g.getName());
    };
  }

}


