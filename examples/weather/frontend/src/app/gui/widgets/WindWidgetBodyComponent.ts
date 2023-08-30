/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {WindWidgetBodyComponentTOP} from "@src/gui/widgets/WindWidgetBodyComponentTOP";

@Component({
  selector: 'wind-widget-body',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/WindWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./WindWidgetBody.scss']
})

export class WindWidgetBodyComponent extends WindWidgetBodyComponentTOP {

}


