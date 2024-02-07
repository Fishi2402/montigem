import {
  Component,
  EventEmitter,
  HostBinding,
  OnInit,
  Output,
} from "@angular/core";
import { GemTextareaInput as GemTextareaInputTOP } from "./gem-textarea-input/gem-textarea-input.component-gen";

@Component({
  selector: "gem-textarea-input",
  templateUrl: "./gem-textarea-input.component.html",
})
export class GemTextareaInput extends GemTextareaInputTOP implements OnInit {
  @HostBinding("attr.id") componentId: string;
  @HostBinding("style.width") width: string = '100%';
  @HostBinding("style.height") height: string = '100%';

  @Output() entryChange = new EventEmitter<string>();
  @Output() public override keyRelease = new EventEmitter<KeyboardEvent>();
  @Output() public override keyPress = new EventEmitter<KeyboardEvent>();

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  ngOnInit(): void {
    this.componentId = this.id;
  }
}
