/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import {HumidityWidgetBodyComponentTOP} from "@src/gui/widgets/HumidityWidgetBodyComponentTOP";

@Component({
  selector: 'humidity-widget-body',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/widgets/HumidityWidgetBody.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./HumidityWidgetBody.scss']
})

export class HumidityWidgetBodyComponent extends HumidityWidgetBodyComponentTOP {


}


