/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { LastPipelineStatComponent,config } from '@src/gui/tv/LastPipelineStatComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
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

  public override getStatusIconStatus(): string {
    if (this.getP()?.getLastPipeline() == null) {
      return '';
    }
    const res: string = this.getP()?.getLastPipeline()?.isSuccessful()
      ? 'success'
      : 'failed';
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

  public override getDurationValue(): string {
    if (this.getP() == null || this.getP().getLastPipeline() == null) {
      return null;
    }
    const totalSeconds = Math.floor(this.getP().getLastPipeline().getDuration() / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');
    const formattedSeconds = seconds.toString().padStart(2, '0');

    return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
  }

}
