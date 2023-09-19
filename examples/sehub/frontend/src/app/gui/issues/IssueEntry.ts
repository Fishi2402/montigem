/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueEntryTOP} from '@src/gui/issues/IssueEntryTOP';

@Directive()

export class IssueEntry extends IssueEntryTOP {

  public promote(event: any): void {
    if (this.getIssue().getState() === IssueState.UNASSIGNED) {
      this.getIssue().setState(IssueState.IN_PROGRESS);
    } else if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
      this.getIssue().setState(IssueState.DONE);
    }
  }

  public demote(event: any): void {
    if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
      this.getIssue().setState(IssueState.UNASSIGNED);
    } else if (this.getIssue().getState() === IssueState.DONE) {
      this.getIssue().setState(IssueState.IN_PROGRESS);
    }
  }

  public override deleteIssue(event: any): void {
    SehubManager.deleteIssue(this.getIssue().getGemId())
  }

}
