/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueCreateTOP} from '@src/gui/issues/IssueCreateTOP';

@Directive

export class IssueCreate extends IssueCreateTOP {

  public ngOnInit(): void {
    super.ngOnInit();
  }

  public override createIssue(event: any): void {
    this.issueBuilder.state(IssueState.UNASSIGNED);
    this.build();
  }

}
