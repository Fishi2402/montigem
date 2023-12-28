/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { RadarBodyComponent,config } from '@src/gui/widgets/RadarBodyComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class RadarBody extends RadarBodyComponent {

}


