/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueCreateTOP} from '@src/gui/issues/IssueCreateTOP';
import { SehubManager } from '@src/sehub/SehubManager';

@Directive()

export class IssueCreate extends IssueCreateTOP {

  public override createIssue(event: any): void {
    SehubManager.issueBuilder().then(gemPromiseRes => {
      this.issueBuilder.state(IssueState.UNASSIGNED);
      this.build();
      this.issueBuilder = gemPromiseRes;
    });
  }

}
