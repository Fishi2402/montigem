/* (c) https://github.com/MontiCore/monticore */

import {OverviewPageComponent} from '@src/gui/montiverse/OverviewPageComponent';
import { Project } from '@src/dtcockpit/Project';

import {
  Component
} from '@angular/core';

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

  public override startPipeline (): (arg: any) => void {
    return event => {
      this.router.navigate(["gui", "montiverse", "Pipeline", this.g.getGemId()]);
    };
  }
}


