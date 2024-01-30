import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  HostBinding,
} from "@angular/core";
import { GemRadioInput as GemRadioInputTOP } from "./gem-radio-input/gem-radio-input.component-gen";

@Component({
  selector: "gem-radio-input",
  templateUrl: "./gem-radio-input.component.html",
})
export class GemRadioInput extends GemRadioInputTOP implements OnInit {
  @HostBinding("attr.id") componentId: string;
  rightPosition: boolean = true;

  // @Input() id: string;

  // @Input() labelText: string;

  // @Input() value: string;

  // @Input() selectedValue: string;

  @Output() selectedValueChange = new EventEmitter<string>();

  onSelectedValueChange(event: string) {
    this.selectedValueChange.emit(event);
  }

  ngOnInit(): void {
    /*
    this.componentId = this.id;
    if (this.labelPosition !== undefined) {
      if (this.labelPosition.toLowerCase() === "left") {
        this.rightPosition = false;
      }
    }
    */
  }

}
