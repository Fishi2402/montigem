/* (c) https://github.com/MontiCore/monticore */
import {ProjectFileViewLinkComponent} from '@src/gui/artifacts/ProjectFileViewLinkComponent';

import {Component,} from '@angular/core';
import {config} from "@src/gui/artifacts/ProjectFileViewLinkStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'project-file-view-link',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/artifacts/ProjectFileViewLink.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ProjectFileViewLink extends ProjectFileViewLinkComponent {

  public override getGemLink1Url (): string  {
     return "gui/ProjectFileView/"+this.getArtifact().getGemId();
  }
}


