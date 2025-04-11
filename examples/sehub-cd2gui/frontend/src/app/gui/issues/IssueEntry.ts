/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from 'sehub/SehubManager';
import {IssueState} from 'sehub/IssueState';
import {Component} from '@angular/core';
import {IssueEntryComponent,config} from 'gui/issues/IssueEntryComponent';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from 'sehub/User';
import {SehubRole} from 'sehub/SehubRole';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class IssueEntry extends IssueEntryComponent {

  public editIssue(): (arg: void) => void {
    return $ => {
      console.log(this.getIssue().getGemId())
      this._router.navigate(['/gui/NotesPage/' + this.getIssue().getGemId()]);
    }
  }

  public promote(): (arg: void) => void {
    return $ => {
      if (this.getIssue().getState() === IssueState.UNASSIGNED) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      } else if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.DONE);
      }
    }
  }

  public demote(): (arg: void) => void {
    return $ => {
      if (this.getIssue().getState() === IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.UNASSIGNED);
      } else if (this.getIssue().getState() === IssueState.DONE) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      }
    }
  }

  public override deleteIssue(): (arg: void) => void {
    return $ => SehubManager.deleteIssue(this.getIssue().getGemId())
  }

  public getIssueCardTitle (): string {
    let postCounter = 0;

    this.getIssue().getPostsList().forEach(p => { postCounter = postCounter + 1 + p.getResponsesList().length})

    return super.getIssueCardTitle() + " (" + postCounter + ")"
  }

}
