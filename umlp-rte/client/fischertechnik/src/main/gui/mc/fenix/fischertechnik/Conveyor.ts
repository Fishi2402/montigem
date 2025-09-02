/* (c) https://github.com/MontiCore/monticore */

import {config, ConveyorComponent} from '../../../../../../build/gui2java2ts/mc/fenix/fischertechnik/ConveyorComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: "Conveyor.html",
})
export class Conveyor extends ConveyorComponent {
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
