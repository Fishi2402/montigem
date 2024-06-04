/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueEntryComponent,config} from '@src/gui/issues/IssueEntryComponent';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class IssueEntry extends IssueEntryComponent {

  public promote(): (arg: void) => void {
    return $ => {
      if (this.getIssue().getState() === IssueState.UNASSIGNED) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      } else if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.DONE);
      }
    };
  }

  public demote(): (arg: void) => void {
    return $ => {
      if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.UNASSIGNED);
      } else if (this.getIssue().getState() === IssueState.DONE) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      }
    };
  }

  public override deleteIssue(): (arg: void) => void {
    return $ => SehubManager.deleteIssue(this.getIssue().getGemId());
  }

  public override navigate(): (arg: void) => void {
    return $ => this.router.navigate(['gui', 'NotesPage', this.issue.getGemId()]);
  }

  public override getDelButtonDisabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    return !!curUser && SehubRole[curUser.getRole()] === "GUEST" ||
                     SehubRole[curUser.getRole()] === "REPORTER";
  }

}
