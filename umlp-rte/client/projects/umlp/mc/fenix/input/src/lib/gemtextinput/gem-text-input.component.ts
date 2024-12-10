import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
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

  @HostBinding("attr.id") componentId: string;

  @HostBinding("style.width")
  @Input()
  width: string = '100%';

  @HostBinding("style.height")
  @Input()
  height: string = '100%';

  @Output() public entryChange = new EventEmitter<string>();
  @Output() public override keyRelease = new EventEmitter<KeyboardEvent>();
  @Output() public override keyPress = new EventEmitter<KeyboardEvent>();

  public override get inputType(): string {
    return this._inputType;
  }

  @Input()
  public override set inputType(inputType: string) {
    if (inputType === "numericText") {
      this._inputType = "text";
      this.isNumeric = true;
    } else {
      this._inputType = inputType;
    }
  }

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  ngOnInit(): void {
    this.componentId = this.id;
  }

}
