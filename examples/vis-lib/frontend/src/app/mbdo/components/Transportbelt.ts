/* (c) https://github.com/MontiCore/monticore */

import {config, TransportbeltComponent} from '@src/mbdo/components/TransportbeltComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class Transportbelt extends TransportbeltComponent {
  @Input()
  @HostBinding("style.--leftDetectorIndicatorOpacity")
  leftDetectorIndicatorOpacity: number;

  override set leftDetectorOn(value: boolean) {
    super.leftDetectorOn = value;
    this.leftDetectorIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--rightDetectorIndicatorOpacity")
  rightDetectorIndicatorOpacity: number;

  override set rightDetectorOn(value: boolean) {
    super.rightDetectorOn = value;
    this.rightDetectorIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--speedIndicatorPosition")
  _speedIndicatorPercentage: number;
}
