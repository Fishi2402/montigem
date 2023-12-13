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

}


