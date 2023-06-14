import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { GemTextInput as GemTextInputTOP } from "./gem-text-input/gem-text-input.component-gen";

@Component({
  selector: "gem-text-input",
  templateUrl: "./gem-text-input.component.html",
  styleUrls: ["./gem-text-input.component.scss"],
})
export class GemTextInput extends GemTextInputTOP implements OnInit {
  // @Input() public id: string;

  // @Input() public labelText: string;

  // @Input() public inputType: string;

  // @Input() public entry: string;

  // @Input() public validity: {
  //   status: string;
  //   message: string;
  // };

  @Output() public entryChange = new EventEmitter<string>();

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  ngOnInit(): void {}
}
