/* (c) https://github.com/MontiCore/monticore */

import {ProjectFileViewComponent,config} from '@src/gui/ProjectFileViewComponent';
import {Component} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';

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

  public constructor(protected route: ActivatedRoute, protected router: Router) {
    super(route, router);
    route.paramMap.subscribe(map => {
      console.warn("_________________________________________")
      this.parentRoutingId = +map.get("parentRoutingId")
      SehubManager.getDirectorySure(this.parentRoutingId).then(gemPromiseRes => {
        this.parent = gemPromiseRes;
      });
    })
  }

  public getGemLink11Url(): string {
    if (this.parent.getContainedBy() != null) {
      return "gui/ProjectFileView/" + this.parent.getContainedBy().getGemId()
    }
    return ""
  }

  public override goToArtifactsPage(event: any): void {
    this.router.navigate(["gui", "ProjectPage", this.parent.getGemId()]).then(() => {
      window.location.reload();
    });
  }

  public override getParentDirGuardCondition(): boolean {
    if (this.parent == null) {
      return false;
    }
    console.warn(this.parent.getContainedBy() != null)
    return !(this.parent.getContainedBy() == null);
  }
}
