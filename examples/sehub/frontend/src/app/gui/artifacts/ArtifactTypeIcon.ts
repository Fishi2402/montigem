/* (c) https://github.com/MontiCore/monticore */
import {ArtifactTypeIconComponent} from '@src/gui/artifacts/ArtifactTypeIconComponent';

import {Component,} from '@angular/core';
import {config} from "@src/gui/artifacts/ArtifactTypeIconStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'artifact-type-icon',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/artifacts/ArtifactTypeIcon.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ArtifactTypeIcon extends ArtifactTypeIconComponent {


  public getGuard0Condition(): boolean {
    return this.getType() === "Directory";
  }

  public getGuard2Condition(): boolean {
    return !(this.getType() === "Directory");
  }


}


