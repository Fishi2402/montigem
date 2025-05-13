/* (c) https://github.com/MontiCore/monticore */

import {config, MultiProcessingStationComponent} from '../../../../build/gui/mc/fenix/fischertechnik/MultiProcessingStationComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: "MultiProcessingStation.html",
})
export class MultiProcessingStation extends MultiProcessingStationComponent {
  @Input()
  @HostBinding("style.--lightBarrierIndicatorOpacity")
  lightBarrierIndicatorOpacity: number;

  override set lightBarrier(value: boolean) {
    super.lightBarrier = value;
    this.lightBarrierIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--lightBarrierIndicatorOvenOpacity")
  lightBarrierIndicatorOvenOpacity: number;

  override set lightBarrierOven(value: boolean) {
    super.lightBarrierOven = value;
    this.lightBarrierIndicatorOvenOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--pusherIndicatorOpacity")
  pusherIndicatorOpacity: number;

  override set pusher(value: boolean) {
    super.pusher = value;
    this.pusherIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--gripperIndicatorOpacity")
  gripperIndicatorOpacity: number;

  override set gripper(value: boolean) {
    super.gripper = value;
    this.gripperIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--positionIndicatorPosition")
  _positionIndicatorPercentage: number;

  @Input()
  @HostBinding("style.--beltSpeedIndicatorPosition")
  _beltSpeedIndicatorPercentage: number;

  @Input()
  @HostBinding("style.--drillSpeedIndicatorPosition")
  _drillSpeedIndicatorPercentage: number;

  @Input()
  @HostBinding("style.--rotationIndicatorDeg")
  _rotationIndicatorDeg: number;

}
