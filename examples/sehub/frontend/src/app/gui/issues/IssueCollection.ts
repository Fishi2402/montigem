/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCollectionTOP} from '@src/gui/issues/IssueCollectionTOP';

@Component({
  selector: 'issue-collection',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueCollection.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class IssueCollection extends IssueCollectionTOP {

  public editIssue(event: any): void {
    console.log(this.getIssue().getGemId())
    this.router.navigate(['/notes/NotesPage/' + this.getIssue().getGemId()]);
  }

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

  public  getIssueCardTitle (): string

  {
    let postCounter = 0;

    this.getIssue().getPosts().forEach(p => { postCounter = postCounter + 1 + p.getResponses().length})

    return super.getIssueCardTitle() + " (" + postCounter + ")"
  }

}
