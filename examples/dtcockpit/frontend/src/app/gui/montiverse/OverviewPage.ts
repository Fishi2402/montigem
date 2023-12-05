

/* (c) https://github.com/MontiCore/monticore */


import {OverviewPageComponent} from '@src/gui/montiverse/OverviewPageComponent';
import { Project } from '@src/dtcockpit/Project';



import {
  Component
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import {DtcockpitSecurityManager} from "../../dtcockpit/DtcockpitSecurityManager";


@Component({
  selector: 'overview-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/montiverse/OverviewPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class OverviewPage extends OverviewPageComponent {

  public override showBranches (g: Project): (arg: any) => void {
    return event => {
      this.router.navigate(["gui", "montiverse", "ShowBranches", g.getGemId()]);
    };
  }

  public override startPipeline (g: Project): (arg: any) => void {
    return event => {
      console.error(g)
      this.router.navigate(["gui", "montiverse", "Pipeline", g.getGemId()]);
    };
  }
}


