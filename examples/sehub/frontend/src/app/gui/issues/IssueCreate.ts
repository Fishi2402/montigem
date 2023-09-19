/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Directive} from '@angular/core';
import {IssueCreateTOP} from '@src/gui/issues/IssueCreateTOP';
import { SehubManager } from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';

@Directive()

export class IssueCreate extends IssueCreateTOP {

  public override createIssue(event: any): void {
    SehubManager.issueBuilder().then(gemPromiseRes => {
      this.issueBuilder.state(IssueState.UNASSIGNED);
      this.build();
      this.issueBuilder = gemPromiseRes;
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
