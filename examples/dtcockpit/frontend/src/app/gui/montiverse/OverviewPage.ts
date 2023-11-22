

/* (c) https://github.com/MontiCore/monticore */


import {OverviewPageTOP} from '@src/gui/montiverse/OverviewPageTOP';
import { GemList } from '@umlp/common';
import { App } from '@src/dtcockpit/App';
import { DtcockpitManager } from '@src/dtcockpit/DtcockpitManager';
import { Group } from '@src/dtcockpit/Group';
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


@Directive()
export  class OverviewPage extends OverviewPageTOP {

  public override showBranches (g: any): (arg: any) => void {
    this.router.navigate(["..", "gui", "montiverse", "ShowBranches", g.getGemId()]);
    return event => {};
  }
}


