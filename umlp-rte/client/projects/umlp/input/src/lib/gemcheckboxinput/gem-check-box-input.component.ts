import {
  Component,
  OnInit,
  EventEmitter,
  Output,
  HostBinding,
  Input,
} from "@angular/core";
import { GemCheckBoxInput as GemCheckBoxInputTOP } from "./gem-check-box-input/gem-check-box-input.component-gen";

@Component({
  selector: "gem-check-box-input",
  templateUrl: "./gem-check-box-input.component.html",
})
export class GemCheckBoxInput extends GemCheckBoxInputTOP implements OnInit {
  @HostBinding("attr.id") componentId: string;
  rightPosition: boolean = true;

  // @Input() public id: string;

  // @Input() public labelText: string;

  // @Input() public checked: boolean = false;

  @Output() checkedChange = new EventEmitter<boolean>();

  onCheckedChange(event: boolean) {
    this.checkedChange.emit(event);
  }

  public override get labelPosition(): string {
    return this._labelPosition;
  }

  @Input()
  public override set labelPosition(labelPosition: string) {
    if (labelPosition === 'left') {
      this.rightPosition = false;
    } else {
      this.rightPosition = true;
    }
  }

  ngOnInit(): void {
    /*
    this.componentId = this.id;
    if (this.labelPosition !== undefined) {
      if (this.labelPosition.toLowerCase() === "left") {
        this.rightPosition = false;
      }
    }*/
  }
}
