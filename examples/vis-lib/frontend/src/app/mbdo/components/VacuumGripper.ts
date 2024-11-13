/* (c) https://github.com/MontiCore/monticore */

import {config, VacuumGripperComponent} from '@src/mbdo/components/VacuumGripperComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
    standalone: true,
    imports: [...config.imports],
    selector: config.selector,
    templateUrl: config.templateUrl,
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
    @HostBinding("style.--rotationDeg")
    _rotationDeg: number;


    @Input()
    @HostBinding("style.--verticalPercentage")
    _verticalPercentage: number;

    @Input()
    @HostBinding("style.--horizontalPercentage")
    _horizontalPercentage: number;
}
