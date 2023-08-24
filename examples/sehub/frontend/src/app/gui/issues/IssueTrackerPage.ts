/* (c) https://github.com/MontiCore/monticore */

import {Issue} from '@src/sehub/Issue';
import {Component} from '@angular/core';
import {IssueTrackerPageTOP} from '@src/gui/issues/IssueTrackerPageTOP';
import {GemList} from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';
import {IssueGemList} from '@src/sehub/gemlists/IssueGemList';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {ActivatedRoute, Router} from '@angular/router';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Issue.scss"]
})

export class IssueTrackerPage extends IssueTrackerPageTOP {

  private todo: GemList<Issue>;

  private doing: GemList<Issue>;

  private done: GemList<Issue>;

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    }
  }

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }


  public getTodoTitle (): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {if(i.getState() == 0) count++})
    return super.getTodoTitle() + " (" + count + ")"
  }

  public  getDoingTitle (): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {if(i.getState() == 1) count++})
    return super.getDoingTitle() + " (" + count + ")"
  }

  public  getDoneTitle (): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {if(i.getState() == 2) count++})
    return super.getDoneTitle() + " (" + count + ")"
  }

}


