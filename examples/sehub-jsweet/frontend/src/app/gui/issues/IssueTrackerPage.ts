/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueTrackerPageTOP} from '@src/gui/issues/IssueTrackerPageTOP';

@Directive()

export class IssueTrackerPage extends IssueTrackerPageTOP {

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


