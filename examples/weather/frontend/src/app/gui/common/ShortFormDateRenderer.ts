import {Directive} from '@angular/core';
import {ShortFormDateRendererTOP} from "@src/gui/common/ShortFormDateRendererTOP";

@Directive()
export  class ShortFormDateRenderer extends ShortFormDateRendererTOP {
  getGemText0Value(): string {
    let today = new Date();
    let date = this.getDay()?.getDate();

    if(date == null){
      return "???";
    }else{
      if(this.getReplaceToday()
         && today.getFullYear() == date.getFullYear()
         && today.getMonth() == date.getMonth()
         && today.getDate() == date.getDate()
      ){
        return "Today";
      }else{
        let res = date.toLocaleDateString("en-US", {
          weekday: "short",
        });
        return res == null ? "???" : res;
      }
    }
  }
}
