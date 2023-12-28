/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {ShortFormDateRendererComponent,config} from "@src/gui/common/ShortFormDateRendererComponent";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
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
