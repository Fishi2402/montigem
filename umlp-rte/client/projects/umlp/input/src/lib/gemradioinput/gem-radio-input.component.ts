import { Component, OnInit, Output, EventEmitter } from "@angular/core";
import { GemRadioInput as GemRadioInputTOP } from "./gem-radio-input/gem-radio-input.component-gen";

@Component({
  selector: "gem-radio-input",
  templateUrl: "./gem-radio-input.component.html",
  styleUrls: ["./gem-radio-input.component.scss"],
})
export class GemRadioInput extends GemRadioInputTOP implements OnInit {
  // @Input() id: string;

  // @Input() labelText: string;

  // @Input() value: string;

  // @Input() selectedValue: string;

  // @Input() isChecked: boolean = false;

  @Output() selectedValueChange = new EventEmitter<string>();

  onSelectedValueChange(newValue: string) {
    this.selectedValueChange.emit(newValue);
  }


  ngOnInit(): void {}
}
