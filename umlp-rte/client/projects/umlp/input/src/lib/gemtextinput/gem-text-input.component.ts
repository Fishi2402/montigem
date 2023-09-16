import {
  Component,
  EventEmitter,
  HostBinding,
  OnInit,
  Output,
} from "@angular/core";
import { GemTextInput as GemTextInputTOP } from "./gem-text-input/gem-text-input.component-gen";

@Component({
  selector: "gem-text-input",
  templateUrl: "./gem-text-input.component.html",
})
export class GemTextInput extends GemTextInputTOP implements OnInit {
  isNumeric: boolean = false;
  isDropdown: boolean = false;

  @HostBinding("attr.id") componentId: string;

  @Output() public entryChange = new EventEmitter<string>();
  @Output() public keyup = new EventEmitter<KeyboardEvent>();
  @Output() public keydown = new EventEmitter<KeyboardEvent>();

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  ngOnInit(): void {
    this.componentId = this.id;

    if (this.inputType === "numericText") {
      this.inputType = "text";
      this.isNumeric = true;
    } else if (this.inputType === "dropdown") {
      this.inputType = "text";
      this.isDropdown = true;
    }
  }
}
