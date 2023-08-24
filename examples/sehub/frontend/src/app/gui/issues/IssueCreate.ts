/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateTOP} from '@src/gui/issues/IssueCreateTOP';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'issue-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class IssueCreate extends IssueCreateTOP {

  public ngOnInit(): void {
    super.ngOnInit();
  }

  public override createIssue(event: any): void {
    this.issueBuilder.state(IssueState.UNASSIGNED);
    this.build();
  }

}
