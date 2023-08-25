/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueTrackerPageTOP} from '@src/gui/issues/IssueTrackerPageTOP';
import {ActivatedRoute, Router} from '@angular/router';
import { SehubSecurityManager } from '@src/sehub/SehubSecurityManager';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Issue.scss"]
})

export class IssueTrackerPage extends IssueTrackerPageTOP {

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    }
    super.ngOnInit();
  }

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  public override getTodoTitle(): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {
      if (i.getState() === IssueState.UNASSIGNED) count++
    })
    return super.getTodoTitle() + " (" + count + ")"
  }

  public override getDoingTitle(): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {
      if (i.getState() === IssueState.IN_PROGRESS) count++
    })
    return super.getDoingTitle() + " (" + count + ")"
  }

  public override getDoneTitle(): string {
    let count = 0;
    this.getIssues().toArray().forEach(i => {
      if (i.getState() === IssueState.DONE) count++
    })
    return super.getDoneTitle() + " (" + count + ")"
  }

}


