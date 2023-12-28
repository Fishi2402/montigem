/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateComponent} from '@src/gui/issues/IssueCreateComponent';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'issue-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class IssueCreate extends IssueCreateComponent {

  public override createIssue(event: any): void {
    SehubManager.issueBuilder().then(gemPromiseRes => {
      this.issue.state(IssueState.UNASSIGNED);
      this.build();
      this.issue = gemPromiseRes;
    });
  }

}
