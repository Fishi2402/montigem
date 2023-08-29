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
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'project-file-view',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/ProjectFileView.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class ProjectFileView extends ProjectFileViewTOP {


  public constructor(protected route: ActivatedRoute, protected router: Router) {
    super(route, router);
    route.paramMap.subscribe(map => {
      console.warn("_________________________________________")
      this.parentRoutingId = +map.get("parentRoutingId")
      SehubManager.getDirectorySure(this.parentRoutingId).then(gemPromiseRes => {
        this.parent = gemPromiseRes;
      });
      const gemTable1Columns: Array<any> = [];
      gemTable1Columns.push(new TableColumn("_name","Artifact Name"));
      gemTable1Columns.push(new TableColumn("_modified","Last update"));
      this.setGemTable1Columns(gemTable1Columns);
    })
  }

  public override ngOnInit(): void {
    // Do nothing, because subscribing to paramMap already handles this stuff
  }

  public override navigateToSubPage (row: Artifact): void {
    if(row instanceof Directory) {
      this.router.navigate(["..", "project", "ProjectFileView", row.getGemId()]);
    }
  }

  public override goToArtifactsPage (event: any): void {
    this.router.navigate(["..", "artifacts", "ProjectPage", this.parentRoutingId]).then(() => {
      window.location.reload();
    });
  }

}


