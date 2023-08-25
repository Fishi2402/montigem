import {Component} from "@angular/core";
import {RangeWithGradientComponentTOP} from "@src/gui/atomic/range_with_gradient/RangeWithGradientComponentTOP";

@Component({
  selector: "range-with-gradient",
  templateUrl: "./RangeWithGradient.html",
})
export class RangeWithGradientComponent extends RangeWithGradientComponentTOP {
  uuid: string;

  ngOnInit() {
    this.uuid = crypto.randomUUID();
  }

}
