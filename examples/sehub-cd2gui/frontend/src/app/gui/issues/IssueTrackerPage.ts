/* (c) https://github.com/MontiCore/monticore */

import {Issue} from '@src/sehub/Issue';
import {IssueState} from '@src/sehub/IssueState';
import {SehubManager} from '@src/sehub/SehubManager';
import {Component} from '@angular/core';
import {IssueTrackerPageTOP} from '@src/gui/issues/IssueTrackerPageTOP';
import {GemList} from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';
import {IssueGemList} from '@src/sehub/gemlists/IssueGemList';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Issue.scss"]
})

export class IssueTrackerPage extends IssueTrackerPageTOP {

  private todo: GemList<Issue>;

  private doing: GemList<Issue>;

  private done: GemList<Issue>;

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

  public override getTodoIssues(): GemList<Issue> {
    return this.todo;
  }

  public override getDoingIssues(): GemList<Issue> {
    return this.doing;
  }

  public override getDoneIssues(): GemList<Issue> {
    return this.done;
  }

}


