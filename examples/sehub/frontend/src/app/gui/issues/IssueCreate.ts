/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateComponent,config} from '@src/gui/issues/IssueCreateComponent';
import { SehubManager } from '@src/sehub/SehubManager';
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
export class IssueCreate extends IssueCreateComponent {

  public override createIssue(): (arg: void) => void {
    return $ => SehubManager.issueBuilder().then(gemPromiseRes => {
      this.getIssue().setState(IssueState.UNASSIGNED);
      this.getIssue().build();
      this.setIssue(gemPromiseRes);
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
