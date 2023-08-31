/* (c) https://github.com/MontiCore/monticore */

import {Artifact} from '@src/sehub/Artifact';
import {} from '@monticore/montigem-charts';
import {ProjectFileViewTOP} from '@src/gui/ProjectFileViewTOP';
import {GemTreeviewEntry} from '@monticore/montigem-basic';
import {Directory} from '@src/sehub/Directory';
import {SehubManager} from '@src/sehub/SehubManager';
import {Project} from '@src/sehub/Project';
import {TableColumn} from '@monticore/montigem-table';

import {
  Directive,
} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';

@Directive()

export class ProjectFileView extends ProjectFileViewTOP {

  public override goToSuperpage(parent: any): (arg: any) => void {
    return event => {
      this.router.navigate(["..", "project", "ProjectFileView", this.parent.getContainedBy().getGemId()]);
    };

  }

  public override goToSubpage(artifact: any): (arg: any) => void {
    return event => {
      this.router.navigate(["..", "project", "ProjectFileView", artifact.getGemId()]);
    };

  }

  public override goToArtifactsPage(event: any): void {
    this.router.navigate(["..", "artifacts", "ProjectPage", this.parent.getGemId()]).then(() => {
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
