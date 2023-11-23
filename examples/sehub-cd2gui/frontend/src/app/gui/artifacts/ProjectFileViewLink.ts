/* (c) https://github.com/MontiCore/monticore */
import {ProjectFileViewLinkComponent} from '@src/gui/artifacts/ProjectFileViewLinkComponent';

import {Component,} from '@angular/core';


@Component({
  selector: 'project-file-view-link',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/artifacts/ProjectFileViewLink.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ProjectFileViewLink extends ProjectFileViewLinkComponent {

  public override getGemLink1Url (): string  {
     return "/gui/ProjectFileView/"+this.getArtifact().getGemId();
  }
}


