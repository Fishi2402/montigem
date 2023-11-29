/* (c) https://github.com/MontiCore/monticore */

import { Group } from '@src/dtcockpit/Group';
import { Project } from '@src/dtcockpit/Project';
import { DtCockpitNavigationTOP } from '@src/gui/navigation/DtCockpitNavigationTOP';
import { Directive } from '@angular/core';

@Directive()
export class DtCockpitNavigation extends DtCockpitNavigationTOP {

  public getProjectNavTarget(p: Project): string {
    return "/gui/pipelines/PipelineStats/" + p.getGemId();
  }

  public getGrouptNavTarget(g: Group): string {
    return "/gui/pipelines/PipelineGroupStats/" + g.getGemId();
  }

  public  getMontiVerseNameTarget (p: Project): string
  {
    return "/gui/montiverse/OverviewPage/" + p.getGemId();
  }

}


