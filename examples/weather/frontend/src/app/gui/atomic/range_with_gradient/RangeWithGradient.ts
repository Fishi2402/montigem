/* (c) https://github.com/MontiCore/monticore */
import {Component} from "@angular/core";
import {RangeWithGradientComponent} from "@src/gui/atomic/range_with_gradient/RangeWithGradientComponent";

@Component({
  selector: "range-with-gradient",
  templateUrl: "./RangeWithGradient.html",
})
export class RangeWithGradient extends RangeWithGradientComponent {
  uuid: string;

  ngOnInit() {
    this.uuid = crypto.randomUUID();
  }

}
