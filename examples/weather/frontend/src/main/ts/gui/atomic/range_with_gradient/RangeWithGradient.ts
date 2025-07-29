/* (c) https://github.com/MontiCore/monticore */
import {Component} from "@angular/core";
import {RangeWithGradientComponent,config} from "gui/atomic/range_with_gradient/RangeWithGradientComponent";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: 'RangeWithGradient.html',
})
export class RangeWithGradient extends RangeWithGradientComponent {
  uuid: string;

  override init(): void {
    super.init();
    this.uuid = crypto.randomUUID();
  }

}
