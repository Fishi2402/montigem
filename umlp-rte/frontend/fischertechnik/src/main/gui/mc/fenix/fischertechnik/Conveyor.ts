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
  @HostBinding("style.--swapDetectorIndicatorOpacity")
  swapDetectorIndicatorOpacity: number;

  override set swapDetectorOn(value: boolean) {
    super.swapDetectorOn = value;
    this.swapDetectorIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--feedDetectorIndicatorOpacity")
  feedDetectorIndicatorOpacity: number;

  override set feedDetectorOn(value: boolean) {
    super.feedDetectorOn = value;
    this.feedDetectorIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveToFeed")
  moveToFeedOn: number;

  override set moveToFeed(value: boolean) {
    super.moveToFeed = value;
    this.moveToFeedOn = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveToSwap")
  moveToSwapOn: number;

  override set moveToSwap(value: boolean) {
    super.moveToSwap = value;
    this.moveToSwapOn = value ? 1 : 0;
  }
}
