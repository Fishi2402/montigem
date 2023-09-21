/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueEntryTOP} from '@src/gui/issues/IssueEntryTOP';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import { Issue } from '@src/sehub/Issue';

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

  public override navigate(): void {
    this.router.navigate(['notes', 'NotesPage', this.issue.getGemId()]);
  }

  public override getDelButtonDisabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "GUEST" ||
                     SehubRole[curUser.getRole()] === "REPORTER") {
      return true;
    }
    return false;
  }

}
