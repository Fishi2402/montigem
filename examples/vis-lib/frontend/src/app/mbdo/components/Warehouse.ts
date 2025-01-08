/* (c) https://github.com/MontiCore/monticore */

import {config, WarehouseComponent} from '@src/mbdo/components/WarehouseComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
    standalone: true,
    imports: [...config.imports],
    selector: config.selector,
    templateUrl: config.templateUrl,
})
export class Warehouse extends WarehouseComponent {

    @Input()
    @HostBinding("style.--depthPercentage")
    _depthPercentage: number;


    @Input()
    @HostBinding("style.--verticalPercentage")
    _verticalPercentage: number;

    @Input()
    @HostBinding("style.--horizontalPercentage")
    _horizontalPercentage: number;
}
