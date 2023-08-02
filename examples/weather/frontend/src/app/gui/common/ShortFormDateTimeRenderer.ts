import {Component} from '@angular/core';
import {ShortFormDateRendererTOP} from "@src/gui/common/ShortFormDateRendererTOP";
import {ShortFormDateTimeRendererTOP} from "@src/gui/common/ShortFormDateTimeRendererTOP";

@Component({
  selector: 'short-form-date-time-renderer',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/common/ShortFormDateTimeRenderer.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ShortFormDateTimeRenderer extends ShortFormDateTimeRendererTOP {
  getGemText0Value(): string {
    let today = new Date();
    let date = this.getForecast()?.getDate();

    if(date == null){
      return "???";
    }else{
      const hours = date.toLocaleTimeString("en-US", {hour: "2-digit"})
      if(this.getReplaceToday()
         && today.getFullYear() == date.getFullYear()
         && today.getMonth() == date.getMonth()
         && today.getDate() == date.getDate()
      ){
        return "Today "+hours;
      }else{
        let res = date.toLocaleDateString("en-US", {
          weekday: "short",
        });
        return res == null ? "???" : res+ " "+hours;
      }
    }
  }
}
