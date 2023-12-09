/* (c) https://github.com/MontiCore/monticore */
import {Component} from "@angular/core";
import {RangeWithGradientComponent} from "@src/gui/atomic/range_with_gradient/RangeWithGradientComponent";
import {config} from "@src/gui/atomic/range_with_gradient/RangeWithGradientStandaloneConfig"
@Component({
  standalone:true,
  imports:[... config.imports],
  selector: "range-with-gradient",
  templateUrl: "./RangeWithGradient.html",
})
export class RangeWithGradient extends RangeWithGradientComponent {
  uuid: string;

  ngOnInit() {
    this.uuid = crypto.randomUUID();
  }

}
