/* (c) https://github.com/MontiCore/monticore */

import { ProcessModelsComponent, config } from '../../../../build/frontendGen/build/appgui/gui/editprocessmodels/ProcessModelsComponent';

import {
  Component,
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProcessModels extends ProcessModelsComponent {
}
