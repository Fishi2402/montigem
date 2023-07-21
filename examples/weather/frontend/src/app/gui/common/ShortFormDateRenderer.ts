import {Component} from '@angular/core';
import {ShortFormDateRendererTOP} from "@src/gui/common/ShortFormDateRendererTOP";

@Component({
  selector: 'short-form-date-renderer',
  templateUrl: '../../../../build/appgui/gui/common/ShortFormDateRenderer.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ShortFormDateRenderer extends ShortFormDateRendererTOP {
  getGemText0Value(): string {
    let res = this.getDay()?.getDate()?.toLocaleDateString("en-US", {
      weekday: "short",
    });

    return res == null ? "???" : res;
  }
}
