import { Component, Input } from "@angular/core";

@Component({
  selector: "gem-number-input",
  templateUrl: "./gem-number-input.component.html",
  styleUrls: ["./gem-number-input.component.scss"]
})
export class GemNumberInput {

  @Input()
  label: string = "";

  @Input()
  value: number | null = null;

}