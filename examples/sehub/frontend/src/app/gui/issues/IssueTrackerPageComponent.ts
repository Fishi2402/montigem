/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { IssueTrackerPageComponentTOP } from '@src/gui/issues/IssueTrackerPageComponentTOP';
import { Issue } from '@src/sehub/Issue';
import { IssueState } from '@src/sehub/IssueState';
import { SehubManager } from '@src/sehub/SehubManager';
import { SehubSecurityManager } from '@src/sehub/SehubSecurityManager';
import { IssueGemList } from '@src/sehub/gemlists/IssueGemList';
import { GemList } from '@umlp/common';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Issue.scss"]
})

export class IssueTrackerPageComponent extends IssueTrackerPageComponentTOP {

  protected todoList: GemList<Issue> = new IssueGemList([]).build();
  protected doingList: GemList<Issue> = new IssueGemList([]).build();
  protected doneList: GemList<Issue> = new IssueGemList([]).build();

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    }
    super.ngOnInit();
  }

  public override getTodoListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.todoList;
    }
    for (const i of this.todoList) {
      if (!this.getIssues().contains(i.getGemId())) {
        this.todoList.removeIdSure(i.getGemId());
      }
    }
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.UNASSIGNED && !this.todoList.contains(i.getGemId())) {
        this.todoList.push(i);
      }
    }
    return this.todoList;
  }

  public override getDoingListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.doingList;
    }
    for (const i of this.doingList) {
      if (!this.getIssues().contains(i.getGemId())) {
        this.doingList.removeIdSure(i.getGemId());
      }
    }
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.IN_PROGRESS && !this.doingList.contains(i.getGemId())) {
        this.doingList.push(i);
      }
    }
    return this.doingList;
  }

  public override getDoneListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.doneList;
    }
    for (const i of this.doneList) {
      if (!this.getIssues().contains(i.getGemId())) {
        this.doneList.removeIdSure(i.getGemId());
      }
    }
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.DONE && !this.doneList.contains(i.getGemId())) {
        this.doneList.push(i);
      }
    }
    return this.doneList;
  }

  public todoListDrop(issue: Issue): void {
    // TODO AGe: doesn't work
    if (issue) {
      issue.setState(IssueState.UNASSIGNED);
    }
  }

  public doingListDrop(issue: Issue): void {
    if (issue) {
      issue.setState(IssueState.IN_PROGRESS);
    }
  }

  public doneListDrop(issue: Issue): void {
    if (issue) {
      issue.setState(IssueState.DONE);
    }
  }

}
