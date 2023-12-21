import { Component, HostBinding, Input } from "@angular/core";
import { GemRow as GemRowTOP } from "./gem-row/gem-row.component-gen";

@Component({
  selector: "gem-row",
  templateUrl: "./gem-row.component.html",
  styleUrls: ["./gem-row.component.scss"],
})
export class GemRow extends GemRowTOP {
  @HostBinding('style.height')
  protected override _height: string = '100%';

  @HostBinding('style.width')
  protected override _width: string = '100%';

  @HostBinding('style.justify-content')
  protected override _hAlign: string = "stretch";

  @HostBinding('style.align-content')
  @HostBinding('style.align-items')
  protected override _vAlign: string = "stretch";

  @HostBinding('style.row-gap')
  protected override _rowGap: string = "0";

  @HostBinding('style.column-gap')
  protected override _colGap: string = "0";

  @HostBinding('style.flex-wrap')
  @Input()
  public wrap: string = 'wrap';

  public override get hAlign(): string {
    return this._hAlign;
  }

  @Input()
  public override set hAlign(hAlign: string) {
    if (hAlign === 'left') {
      this._hAlign = 'flex-start'
    }
    if (hAlign === 'right') {
      this._hAlign = 'flex-end'
    }
    else {
      this._hAlign = hAlign;
    }
  }

  public override get vAlign(): string {
    return this._vAlign;
  }

  @Input()
  public override set vAlign(vAlign: string) {
    if (vAlign === 'top') {
      this._vAlign = 'flex-start'
    }
    if (vAlign === 'bottom') {
      this._vAlign = 'flex-end'
    }
    else {
      this._vAlign = vAlign;
    }
  }
}
