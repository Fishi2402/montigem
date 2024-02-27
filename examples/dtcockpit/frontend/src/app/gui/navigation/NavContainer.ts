/* (c) https://github.com/MontiCore/monticore */

import { Group } from '@src/dtcockpit/Group';
import { Project } from '@src/dtcockpit/Project';
import { NavContainerComponent, config } from '@src/gui/navigation/NavContainerComponent';

import { Component } from '@angular/core';
// import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class NavContainer extends NavContainerComponent {

  public getProjectNavTarget(p: Project): string {
    return "/gui/pipelines/PipelineStats/" + p.getGemId();
  }

  public getGrouptNavTarget(g: Group): string {
    return "/gui/pipelines/PipelineGroupStats/" + g.getGemId();
  }

  public getMontiVerseNameTarget(p: Project): string {
    return "/gui/montiverse/OverviewPage/" + p.getGemId();
  }

}


