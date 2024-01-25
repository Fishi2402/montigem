import { Component } from "@angular/core";
import { InfoBoxComponent } from "@src/stanley/my/InfoBoxComponent";
import {config} from "@src/stanley/my/InfoBoxStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'info-box',
  templateUrl: './InfoBox.html',
  styleUrls: ['./InfoBox.scss'],
})
export class InfoBox extends InfoBoxComponent {
}
