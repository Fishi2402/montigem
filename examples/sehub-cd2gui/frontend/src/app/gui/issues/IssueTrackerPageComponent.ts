/* (c) https://github.com/MontiCore/monticore */

import {Issue} from '@src/sehub/Issue';
import {IssueState} from '@src/sehub/IssueState';
import {SehubManager} from '@src/sehub/SehubManager';
import {Component} from '@angular/core';
import {IssueTrackerPageComponentTOP} from '@src/gui/issues/IssueTrackerPageComponentTOP';
import {GemList} from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';
import {IssueGemList} from '@src/sehub/gemlists/IssueGemList';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Issue.scss"]
})

export class IssueTrackerPageComponent extends IssueTrackerPageComponentTOP {
  public ngOnInit(): void {
    SehubManager.getIssueList().then(gemPromiseRes => {
      this.issues = gemPromiseRes;

      let ids: number[] = [];
      this.todo = new IssueGemList(ids).build();
      this.doing = new IssueGemList(ids).build();
      this.done = new IssueGemList(ids).build();

      this.issues.toArray().forEach(issue => {
        if (issue.getState() === IssueState.UNASSIGNED) {
          this.todo.push(issue);
        }
        if (issue.getState() === IssueState.IN_PROGRESS) {
          this.doing.push(issue);
        }
        if (issue.getState() === IssueState.DONE) {
          this.done.push(issue);
        }
      });
    });

  }

}


