/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {
  ShortFormDateTimeRendererComponent,config
} from "@src/gui/common/ShortFormDateTimeRendererComponent";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ShortFormDateTimeRenderer extends ShortFormDateTimeRendererComponent {
  getGemText0Value(): string {
    let today = new Date();
    let date = this.getForecast()?.getDate();

    if(date == null){
      return "???";
    }else{
      const hours = date.toLocaleTimeString("en-US", {hour: "2-digit"})
      if(this.isReplaceToday()
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
