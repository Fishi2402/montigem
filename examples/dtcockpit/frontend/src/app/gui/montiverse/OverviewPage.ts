/* (c) https://github.com/MontiCore/monticore */

import { OverviewPageComponent, config } from '@src/gui/montiverse/OverviewPageComponent';
import { Project } from '@src/dtcockpit/Project';
import { Option } from '@umlp/input';
import { CommandManager } from '@umlp/common';
import { StartMontiVerseLitePipeline } from '@src/dtcockpit/commands/StartMontiVerseLitePipeline';

import {
  Component
} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: 'overview-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/montiverse/OverviewPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class OverviewPage extends OverviewPageComponent {
  public override startPipeline (): (arg: any) => void {
    return event => {
      CommandManager.executeCommand(new StartMontiVerseLitePipeline());
      window.open('https://git.rwth-aachen.de/monticore/montiverseci/-/pipelines', '_blank');
    };
  }

   public override getBranchDropdownOptions (dependentProject: Project): Array<Option> {
    return dependentProject.getBranches().map(it => ({ value: it.getName(), display: it.getName() }));
  }
}


