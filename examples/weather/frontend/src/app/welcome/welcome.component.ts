/* (c) https://github.com/MontiCore/monticore */
// HWC-P

import { Component } from "@angular/core";
import { FavoriteListComponent } from 'gui/widgets/navigation/FavoriteListComponent';
@Component({
  standalone:true,
  imports:[FavoriteListComponent],
  selector: "welcome",
  templateUrl: "./welcome.component.html",
  styleUrls: ["./welcome.component.scss"]
})
export class Welcome {
}
