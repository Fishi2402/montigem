import { Component, Input } from "@angular/core";

@Component({
  selector: "gem-text-input",
  templateUrl: "./gem-text-input.component.html",
  styleUrls: ["./gem-text-input.component.scss"]
})
export class GemTextInput {

  @Input()
  label: string = "";

  @Input()
  value: string = "";
}