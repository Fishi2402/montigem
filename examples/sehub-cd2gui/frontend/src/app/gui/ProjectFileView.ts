/* (c) https://github.com/MontiCore/monticore */

import {ProjectFileViewComponent,config} from 'gui/ProjectFileViewComponent';
import {Component} from '@angular/core';
import {SehubManager} from 'sehub/SehubManager';

import {ActivatedRoute, Router} from '@angular/router';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProjectFileView extends ProjectFileViewComponent {

  public  getGemLink11Url (): string {
    if(this.parent.getContainedBy() != null){
      return "/gui/ProjectFileView/"+ this.parent.getContainedBy().getGemId()
    }
    return ""
  }

  public override goToArtifactsPage(): (arg: void) => void {
    return $ => {
      this._router.navigate(["..", "artifacts", "ProjectPage", this.parent.getGemId()]).then(() => {
        window.location.reload();
      });
    }
  }

  public override getParentDirGuardCondition(): boolean {
    if (this.parent == null) {
      return false;
    }
    console.warn(this.parent.getContainedBy() != null)
    return !(this.parent.getContainedBy() == null);
  }
}
