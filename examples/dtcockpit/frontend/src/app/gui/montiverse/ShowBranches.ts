/* (c) https://github.com/MontiCore/monticore */
import {ShowBranchesTOP} from '@src/gui/montiverse/ShowBranchesTOP';
import { Branch } from '@src/dtcockpit/Branch';
import { DtcockpitManager } from '@src/dtcockpit/DtcockpitManager';
import { Project } from '@src/dtcockpit/Project';

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
export  class ShowBranches extends ShowBranchesTOP {

  public override chooseBranch (g: Branch): (arg: any) => void {
    return event => {
      g.getProject().getCiInfos().addDefaultBranch(0, g.getName());
    };
  }

}


