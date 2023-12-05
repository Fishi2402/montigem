/* (c) https://github.com/MontiCore/monticore */
import {ShowBranchesComponent} from '@src/gui/montiverse/ShowBranchesComponent';
import { Branch } from '@src/dtcockpit/Branch';

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
  selector: 'show-branches',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/montiverse/ShowBranches.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ShowBranches extends ShowBranchesComponent {

  public override chooseBranch (g: Branch): (arg: any) => void {
    return event => {
      g.getProject().getCiInfos().addDefaultBranch(0, g.getName());
    };
  }

}


