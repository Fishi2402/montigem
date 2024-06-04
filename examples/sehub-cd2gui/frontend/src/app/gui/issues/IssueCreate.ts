/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateComponent,config} from '@src/gui/issues/IssueCreateComponent';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class IssueCreate extends IssueCreateComponent {

  public override createIssue(event: any): (arg: void) => void {
    return $ => {
      SehubManager.issueBuilder().then(gemPromiseRes => {
        this.issue.state(IssueState.UNASSIGNED);
        this.build();
        this.issue = gemPromiseRes;
      });
    }
  }

}
