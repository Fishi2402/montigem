/* (c) https://github.com/MontiCore/monticore */

import { Artifact } from '@src/sehub/Artifact';
import {  } from '@monticore/montigem-charts';
import { ProjectFileViewTOP } from '@src/gui/ProjectFileViewTOP';
import { GemTreeviewEntry } from '@monticore/montigem-basic';
import { Directory } from '@src/sehub/Directory';
import { SehubManager } from '@src/sehub/SehubManager';
import { Project } from '@src/sehub/Project';
import { TableColumn } from '@monticore/montigem-table';

import {
  Directive,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Directive()

export class ProjectFileView extends ProjectFileViewTOP {

  public override navigateToSubPage (row: Artifact): void {
    if(row instanceof Directory) {
      this.router.navigate(["..", "project", "ProjectFileView", row.getGemId()]);
    }
  }

  public override goToArtifactsPage (event: any): void {
    this.router.navigate(["..", "artifacts", "ProjectPage", this.parent.getGemId()]).then(() => {
      window.location.reload();
    });
  }

}


