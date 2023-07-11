import { Component, OnInit, EventEmitter, Output } from "@angular/core";
import { GemCheckBoxInput as GemCheckBoxInputTOP } from "./gem-check-box-input/gem-check-box-input.component-gen";

@Component({
  selector: "gem-checkbox-input",
  templateUrl: "./gem-check-box-input.component.html",
  styleUrls: ["./gem-check-box-input.component.scss"],
})
export class GemCheckBoxInput extends GemCheckBoxInputTOP implements OnInit {
  // @Input() public id: string;

  // @Input() public labelText: string;

  // @Input() public checked: boolean = false;

  // @Input() public value: string;

  @Output() checkedChange = new EventEmitter<boolean>();

  onCheckedChange(event: boolean) {
    this.checkedChange.emit(event);
  }

  ngOnInit(): void {}
}
