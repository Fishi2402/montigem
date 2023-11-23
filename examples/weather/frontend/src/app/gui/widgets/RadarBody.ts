/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { RadarBodyComponent } from '@src/gui/widgets/RadarBodyComponent';

@Component({
    selector: 'radar-body',
    templateUrl: './RadarBody.html',
    styles: [":host{ display: flex; flex-direction: column }"],
    styleUrls: ['./WindWidgetBody.scss']
})
export class RadarBody extends RadarBodyComponent {

}


