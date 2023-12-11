/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { LastPipelineStatComponent } from '@src/gui/tv/LastPipelineStatComponent';

@Component({
  selector: 'last-pipeline-stat',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/tv/LastPipelineStat.html',
  styles: [":host{ display: flex; flex-direction: column }"]
})
export class LastPipelineStat extends LastPipelineStatComponent {

  public override getStatusIconSource(): string {
    if (this.getP()?.getLastPipeline() == null) {
      return '';
    }
    const res: string = this.getP()?.getLastPipeline()?.isSuccessful()
      ? 'assets/@gitlab/svgs/sprite_icons/status_success.svg' 
      : 'assets/@gitlab/svgs/sprite_icons/status_failed.svg';
    return res;
  }

  public override getStatusTextValue(): string {
    if (this.getP()?.getLastPipeline() == null) {
      return '';
    }
    const res: string = this.getP()?.getLastPipeline()?.isSuccessful()
      ? 'passed' 
      : 'failed';
    return res;
  }

}
