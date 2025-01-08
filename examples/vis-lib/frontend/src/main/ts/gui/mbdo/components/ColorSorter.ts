/* (c) https://github.com/MontiCore/monticore */

import {config, ColorSorterComponent} from '@src/mbdo/components/ColorSorterComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: "ColorSorter.html",
})
export class ColorSorter extends ColorSorterComponent {
  @Input()
  @HostBinding("style.--pumpIndicatorOpacity")
  pumpIndicatorOpacity: number;

  override set pumpRunning(value: boolean) {
    super.pumpRunning = value;
    this.pumpIndicatorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--pusherIndicator1Opacity")
  pusherIndicator1Opacity: number;

  override set pusherIndicator1On(value: boolean) {
    super.pusherIndicator1On = value;
    this.pusherIndicator1Opacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--pusherIndicator2Opacity")
  pusherIndicator2Opacity: number;

  override set pusherIndicator2On(value: boolean) {
    super.pusherIndicator2On = value;
    this.pusherIndicator2Opacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--pusherIndicator3Opacity")
  pusherIndicator3Opacity: number;

  override set pusherIndicator3On(value: boolean) {
    super.pusherIndicator3On = value;
    this.pusherIndicator3Opacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--speedIndicatorPosition")
  _speedIndicatorPercentage: number;
}
