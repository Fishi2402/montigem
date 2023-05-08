// GEN-P: translated from Java

import { Component, Input } from "@angular/core";

@Component({
  selector: "info-box",
  templateUrl: "./info-box.component.html",
  styleUrls: ["./info-box.component.scss"]
})
export class InfoBox {

  protected _color: string = "#b8d698";

  @Input()
  text: string = "";

  @Input()
  set color(value: string) {
    this._color = value;
  }

  get color(): string {
    return this._color;
  }

}