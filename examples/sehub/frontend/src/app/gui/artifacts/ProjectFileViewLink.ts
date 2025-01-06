/* (c) https://github.com/MontiCore/monticore */
import {ProjectFileViewLinkComponent,config} from 'gui/artifacts/ProjectFileViewLinkComponent';

import {Component,} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ProjectFileViewLink extends ProjectFileViewLinkComponent {

  public override getGemLink1Url (): string  {
     return "gui/ProjectFileView/"+this.getArtifact().getGemId();
  }
}


