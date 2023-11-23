/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {ShortFormDateRendererComponent} from "@src/gui/common/ShortFormDateRendererComponent";

@Component({
  selector: 'short-form-date-renderer',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/common/ShortFormDateRenderer.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class ShortFormDateRenderer extends ShortFormDateRendererComponent {
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
