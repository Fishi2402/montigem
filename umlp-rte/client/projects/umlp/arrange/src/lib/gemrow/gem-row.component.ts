import { Component } from "@angular/core";
import { GemRow as GemRowTOP } from "./gem-row/gem-row.component-gen";

@Component({
  selector: "gem-row",
  templateUrl: "./gem-row.component.html",
  styleUrls: ["./gem-row.component.scss"],
})
export class GemRow extends GemRowTOP {}
