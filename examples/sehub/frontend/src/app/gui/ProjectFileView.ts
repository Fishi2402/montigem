/* (c) https://github.com/MontiCore/monticore */

import { ProjectFileViewTOP } from '@src/gui/ProjectFileViewTOP';
import { Directive, } from '@angular/core';

@Directive()
export class ProjectFileView extends ProjectFileViewTOP {

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
