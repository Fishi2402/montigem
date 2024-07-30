import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from "@angular/core";
import {Option} from "./gem-dropdown-input-types/gem-dropdown-input.types";
import {GemTextInputValidity} from "../gemtextinput";

@Component({
  selector: "gem-dropdown-input",
  templateUrl: "./gem-dropdown-input.component.html"
})
export class GemDropdownInput {

  @HostBinding("attr.id")
  @Input() id: string;

  @HostBinding("style.width")
  @Input() width: string = '100%';

  @HostBinding("style.height")
  @Input() height: string = '100%';

  @Input() labelText: string;
  @Input() entry: string;
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() public keyRelease: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>();
  @Output() public keyPress: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>();

  onEntryChange(event: string): void {
    this.entryChange.emit(event);
  }
}
