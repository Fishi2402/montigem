/* (c) https://github.com/MontiCore/monticore */
import {Component} from "@angular/core";
import {RangeWithGradientComponent,config} from "gui/atomic/range_with_gradient/RangeWithGradientComponent";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class RangeWithGradient extends RangeWithGradientComponent {
  uuid: string;

  ngOnInit() {
    this.uuid = crypto.randomUUID();
  }

}
