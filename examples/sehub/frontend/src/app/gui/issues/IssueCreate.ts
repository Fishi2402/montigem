/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateComponent} from '@src/gui/issues/IssueCreateComponent';
import { SehubManager } from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';


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

  public override getGemButton3Disabled (): boolean {
    return this.creationDisabled();
  }

  public override getGemTextareaInput2Disabled (): boolean {
    return this.creationDisabled();
  }

  public override getGemTextInput1Disabled (): boolean {
    return this.creationDisabled();
  }

  public creationDisabled(): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "GUEST" ||
                     SehubRole[curUser.getRole()] === "REPORTER") {
      return true;
    }
    return false;
  }

}
