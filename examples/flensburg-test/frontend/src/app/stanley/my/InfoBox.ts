import { Component } from "@angular/core";
import { config, InfoBoxComponent } from "stanley/my/InfoBoxComponent";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'info-box',
  templateUrl: './InfoBox.html',
  styleUrls: ['./InfoBox.scss'],
})
export class InfoBox extends InfoBoxComponent {
}
