import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "gem-button",
  templateUrl: "./gem-button.component.html",
  styleUrls: ["./gem-button.component.scss"]
})
export class GemButton {

  @Output()
  leftClick: EventEmitter<MouseEvent> = new EventEmitter();

  @Input()
  text: string = "";

  onClick(event: MouseEvent) {
    this.leftClick.emit(event);
  }

}