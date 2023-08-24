/* (c) https://github.com/MontiCore/monticore */

import {IssueState} from '@src/sehub/IssueState';
import {Component} from '@angular/core';
import {IssueCreateTOP} from '@src/gui/issues/IssueCreateTOP';
import {SehubManager} from "../../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import { Post } from '@src/sehub/Post';

@Component({
  selector: 'issue-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class IssueCreate extends IssueCreateTOP {

  public ngOnInit(): void {
      SehubManager.issueBuilder().then(gemPromiseRes => {
        this.issueBuilder = gemPromiseRes;
      });
    super.ngOnInit();
  }

  public override createIssue(event: any): void {

      this.issueBuilder.posts(new Set<Post>())
      this.issueBuilder.state(IssueState.UNASSIGNED);
      this.build();

      console.log(this.getIssue().getGemId())


  }

}
