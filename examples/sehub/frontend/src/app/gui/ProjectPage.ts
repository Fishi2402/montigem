/* (c) https://github.com/MontiCore/monticore */

import { Artifact } from '@src/sehub/Artifact';
import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '@umlp/common';
import { GemNetworkData } from '@monticore/montigem-charts';
import { TableColumn } from '@monticore/montigem-table';
import { ProjectPageTOP } from '@src/gui/ProjectPageTOP';

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
  selector: 'project-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/ProjectPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class ProjectPage extends ProjectPageTOP {

  public getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }
}


