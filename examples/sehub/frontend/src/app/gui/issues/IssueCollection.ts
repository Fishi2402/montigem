/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
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
    this.router.navigate(['//notes/NotesPage/' + this.getIssue().getGemId()]);
  }

  public promote(event: any): void {
    if (this.getIssue().getState() === 0) {
      this.getIssue().setState(1);
    } else if (this.getIssue().getState() === 1) {
      this.getIssue().setState(2);
    }
  }

  public demote(event: any): void {
    if (this.getIssue().getState() === 1) {
      this.getIssue().setState(0);
    } else if (this.getIssue().getState() === 2) {
      this.getIssue().setState(1);
    }
  }

  public override deleteIssue(event: any): void {
    SehubManager.deleteIssue(this.getIssue().getGemId())
  }

}
