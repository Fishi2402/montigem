/* (c) https://github.com/MontiCore/monticore */
import {ArtifactTypeIconComponent,config} from '@src/gui/artifacts/ArtifactTypeIconComponent';

import {Component,} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ArtifactTypeIcon extends ArtifactTypeIconComponent {


  public getGuard0Condition(): boolean {
    return this.getType() === "Directory";
  }

  public getGuard2Condition(): boolean {
    return !(this.getType() === "Directory");
  }


}


