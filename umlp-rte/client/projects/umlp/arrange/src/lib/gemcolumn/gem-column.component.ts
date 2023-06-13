import { Component } from "@angular/core";
import { GemColumn as GemColumnTOP } from "./gem-column/gem-column.component-gen";

@Component({
  selector: "gem-column",
  templateUrl: "./gem-column.component.html",
  styleUrls: ["./gem-column.component.scss"],
})
export class GemColumn extends GemColumnTOP {
}
