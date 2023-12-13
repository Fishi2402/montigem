/* (c) https://github.com/MontiCore/monticore */

import {PipelineComponent} from '@src/gui/montiverse/PipelineComponent';
import {Project} from '@src/dtcockpit/Project';

import {
  Component
} from '@angular/core';

@Component({
  selector: 'overview-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/montiverse/Pipeline.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class Pipeline extends PipelineComponent {
  public okLeftClick(event: any): void {
    this.project.setCmd("execute");
  }

  public getInfoValue(): string {
    let info: string = "curl --request POST --fail -F token=$gitTriggerMontiVerseCI -F ref=main ";
    this.project.getDependentProjects().forEach((element) => {
      info = info + " -F " + element.getCiInfos().getVariableName() + "=\"" + element.getCiInfos().getBranchName() + "\"";
    });
    info = info + " https://git.rwth-aachen.de/api/v4/projects/91803/trigger/pipeline";
    return info;
  }

}


