/* (c) https://github.com/MontiCore/monticore */

import {config, VacuumGripperComponent} from '../../../../../../build/gui2java2ts/mc/fenix/fischertechnik/VacuumGripperComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
    standalone: true,
    imports: [...config.imports],
    selector: config.selector,
    templateUrl: "VacuumGripper.html",
})
export class VacuumGripper extends VacuumGripperComponent {
    @Input()
    @HostBinding("style.--pumpIndicatorOpacity")
    pumpIndicatorOpacity: number;

    override set pumpRunning(value: boolean) {
        super.pumpRunning = value;
        this.pumpIndicatorOpacity = value ? 1 : 0;
    }

    @Input()
    @HostBinding("style.--valveIndicatorOpacity")
    valveIndicatorOpacity: number;

    override set valveEnabled(value: boolean) {
      super.valveEnabled = value;
      this.valveIndicatorOpacity = value ? 1 : 0;
    }

    @Input()
    @HostBinding("style.--rotationDeg")
    _rotationDeg: number;

    @Input()
    @HostBinding("style.--refSwitchRotation")
    refRotationIndicatorOpacity: number;

    override set refSwitchRotation(value: boolean) {
      super.refSwitchRotation = value;
      this.refRotationIndicatorOpacity = value ? 1 : 0;
    }


    @Input()
    @HostBinding("style.--verticalPercentage")
    _verticalPercentage: number;

    @Input()
    @HostBinding("style.--refSwitchVertical")
    refVerticalIndicatorOpacity: number;

    override set refSwitchVertical(value: boolean) {
      super.refSwitchVertical = value;
      this.refVerticalIndicatorOpacity = value ? 1 : 0;
    }

    @Input()
    @HostBinding("style.--horizontalPercentage")
    _horizontalPercentage: number;

    @Input()
    @HostBinding("style.--refSwitchHorizontal")
    refHorizontalIndicatorOpacity: number;

    override set refSwitchHorizontal(value: boolean) {
      super.refSwitchHorizontal = value;
      this.refHorizontalIndicatorOpacity = value ? 1 : 0;
    }
}
