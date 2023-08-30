/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { RadarBodyComponentTOP } from '@src/gui/widgets/RadarBodyComponentTOP';

@Component({
    selector: 'radar-body',
    templateUrl: './RadarBody.html',
    styles: [":host{ display: flex; flex-direction: column }"],
    styleUrls: ['./WindWidgetBody.scss']
})

export class RadarBodyComponent extends RadarBodyComponentTOP {

}


