/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {IssueTrackerPageComponent,config} from '@src/gui/issues/IssueTrackerPageComponent';
import {Issue} from '@src/sehub/Issue';
import {IssueState} from '@src/sehub/IssueState';
import {SehubSecurityManager} from '@src/sehub/SehubSecurityManager';
import {IssueGemList} from '@src/sehub/gemlists/IssueGemList';
import {GemList} from '@umlp/common';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class IssueTrackerPage extends IssueTrackerPageComponent {

  protected todoList: GemList<Issue> = new IssueGemList([]).build();
  protected doingList: GemList<Issue> = new IssueGemList([]).build();
  protected doneList: GemList<Issue> = new IssueGemList([]).build();

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['gui/login/LoginPage']);
    }
    super.ngOnInit();
  }

  public override getTodoListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.todoList;
    }
    const tmp: Issue[] = [];
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.UNASSIGNED) {
        tmp.push(i);
      }
    }
    if (!this.equalIds(tmp.map(i => i.getGemId()), this.todoList.getIDs())) {
      this.todoList = new IssueGemList([]).build();
      for (let i of tmp) {
        this.todoList.push(i);
      }
    }
    return this.todoList;
  }

  public override getDoingListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.doingList;
    }
    const tmp: Issue[] = [];
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.IN_PROGRESS) {
        tmp.push(i);
      }
    }
    if (!this.equalIds(tmp.map(i => i.getGemId()), this.doingList.getIDs())) {
      this.doingList = new IssueGemList([]).build();
      for (let i of tmp) {
        this.doingList.push(i);
      }
    }
    return this.doingList;
  }

  public override getDoneListIssues(): GemList<Issue> {
    if (!this.getIssues()) {
      return this.doneList;
    }
    const tmp: Issue[] = [];
    for (const i of this.getIssues()) {
      if (i.getState() === IssueState.DONE) {
        tmp.push(i);
      }
    }
    if (!this.equalIds(tmp.map(i => i.getGemId()), this.doneList.getIDs())) {
      this.doneList = new IssueGemList([]).build();
      for (let i of tmp) {
        this.doneList.push(i);
      }
    }
    return this.doneList;
  }

  public todoListDrop(issue: Issue): void {
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

  protected equalIds(arr1: number[], arr2: number[]) {
    const areAllInArr2 = arr1.every((num) => arr2.includes(num));
    const areAllInArr1 = arr2.every((num) => arr1.includes(num));
    return areAllInArr1 && areAllInArr2;
  }

  public override getTodoTitle(): string {
    let count = 0;
    this.getIssues()?.toArray().forEach(i => {
      if (i.getState() === IssueState.UNASSIGNED) count++
    })
    return super.getTodoTitle() + " (" + count + ")"
  }

  public override getDoingTitle(): string {
    let count = 0;
    this.getIssues()?.toArray().forEach(i => {
      if (i.getState() === IssueState.IN_PROGRESS) count++
    })
    return super.getDoingTitle() + " (" + count + ")"
  }

  public override getDoneTitle(): string {
    let count = 0;
    this.getIssues()?.toArray().forEach(i => {
      if (i.getState() === IssueState.DONE) count++
    })
    return super.getDoneTitle() + " (" + count + ")"
  }

}


