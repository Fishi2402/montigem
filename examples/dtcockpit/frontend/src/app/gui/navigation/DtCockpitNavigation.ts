/* (c) https://github.com/MontiCore/monticore */

import { Group } from '@src/dtcockpit/Group';
import { Project } from '@src/dtcockpit/Project';
import { DtCockpitNavigationComponent ,config} from '@src/gui/navigation/DtCockpitNavigationComponent';
import { Component } from '@angular/core';

import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[...config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})

export class DtCockpitNavigation extends DtCockpitNavigationComponent {

  public getProjectNavTarget(p: Project): string {
    return "/gui/pipelines/PipelineStats/" + p.getGemId();
  }

  public getGrouptNavTarget(g: Group): string {
    return "/gui/pipelines/PipelineGroupStats/" + g.getGemId();
  }
}


