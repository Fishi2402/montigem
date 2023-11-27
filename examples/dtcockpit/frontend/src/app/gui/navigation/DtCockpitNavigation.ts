/* (c) https://github.com/MontiCore/monticore */

import { Group } from '@src/dtcockpit/Group';
import { Project } from '@src/dtcockpit/Project';
import { DtCockpitNavigationComponent } from '@src/gui/navigation/DtCockpitNavigationComponent';
import { Component } from '@angular/core';

@Component({
  selector: 'dt-cockpit-navigation',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/navigation/DtCockpitNavigation.html'
  // styleUrls: ["./DtCockpitNavigationComponent.scss"] // File missing?
})export class DtCockpitNavigation extends DtCockpitNavigationComponent {

  public getProjectNavTarget(p: Project): string {
    return "/gui/pipelines/PipelineStats/" + p.getGemId();
  }

  public getGrouptNavTarget(g: Group): string {
    return "/gui/pipelines/PipelineGroupStats/" + g.getGemId();
  }
}


