/* (c) https://github.com/MontiCore/monticore */

import {config, SortingLineComponent} from '../../../../../../build/gui2java2ts/mc/fenix/fischertechnik/SortingLineComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: "SortingLine.html",
})
export class SortingLine extends SortingLineComponent {
  // Pump indicator
  @Input()
  @HostBinding("style.--pumpIndicatorOpacity")
  pumpIndicatorOpacity: number;

  override set pumpRunning(value: boolean) {
    super.pumpRunning = value;
    this.pumpIndicatorOpacity = value ? 1 : 0;
  }

  // Pusher 1 indicator
  @Input()
  @HostBinding("style.--pusherIndicator1Opacity")
  pusherIndicator1Opacity: number;

  override set pusher1Extended(value: boolean) {
    super.pusher1Extended = value;
    this.pusherIndicator1Opacity = value ? 1 : 0;
  }

  // Pusher 2 indicator
  @Input()
  @HostBinding("style.--pusherIndicator2Opacity")
  pusherIndicator2Opacity: number;

  override set pusher2Extended(value: boolean) {
    super.pusher2Extended = value;
    this.pusherIndicator2Opacity = value ? 1 : 0;
  }

  // Pusher 3 indicator
  @Input()
  @HostBinding("style.--pusherIndicator3Opacity")
  pusherIndicator3Opacity: number;

  override set pusher3Extended(value: boolean) {
    super.pusher3Extended = value;
    this.pusherIndicator3Opacity = value ? 1 : 0;
  }

  // Belt indicator
  @Input()
  @HostBinding("style.--beltIndicatorOpacity")
  beltIndicatorOpacity: number;

  override set beltRunning(value: boolean) {
    super.beltRunning = value;
    this.beltIndicatorOpacity = value ? 1 : 0;
  }

  // Barrier indicator 1
  @Input()
  @HostBinding("style.--barrierIndicator1Opacity")
  barrierIndicator1Opacity: number;

  override set barrierIndicator1On(value: boolean) {
    super.barrierIndicator1On = value;
    this.barrierIndicator1Opacity = value ? 1 : 0;
  }

  // Barrier indicator 2
  @Input()
  @HostBinding("style.--barrierIndicator2Opacity")
  barrierIndicator2Opacity: number;

  override set barrierIndicator2On(value: boolean) {
    super.barrierIndicator2On = value;
    this.barrierIndicator2Opacity = value ? 1 : 0;
  }

  // Barrier indicator 3
  @Input()
  @HostBinding("style.--barrierIndicator3Opacity")
  barrierIndicator3Opacity: number;

  override set barrierIndicator3On(value: boolean) {
    super.barrierIndicator3On = value;
    this.barrierIndicator3Opacity = value ? 1 : 0;
  }

  // Barrier indicator feed 1
  @Input()
  @HostBinding("style.--barrierIndicatorFeed1Opacity")
  barrierIndicatorFeed1Opacity: number;

  override set barrierIndicatorFeed1On(value: boolean) {
    super.barrierIndicatorFeed1On = value;
    this.barrierIndicatorFeed1Opacity = value ? 1 : 0;
  }

  // Barrier indicator feed 2
  @Input()
  @HostBinding("style.--barrierIndicatorFeed2Opacity")
  barrierIndicatorFeed2Opacity: number;

  override set barrierIndicatorFeed2On(value: boolean) {
    super.barrierIndicatorFeed2On = value;
    this.barrierIndicatorFeed2Opacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--colorIndicator")
  _colorSensor: string;
}
