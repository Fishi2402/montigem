/* (c) https://github.com/MontiCore/monticore */

import {Issue} from '@src/sehub/Issue';
import {IssueState} from '@src/sehub/IssueState';
import {SehubManager} from '@src/sehub/SehubManager';
import {Directive} from '@angular/core';
import {IssueTrackerPageTOP} from '@src/gui/issues/IssueTrackerPageTOP';
import {GemList} from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';
import {IssueGemList} from '@src/sehub/gemlists/IssueGemList';

@Directive()

export class IssueTrackerPage extends IssueTrackerPageTOP {

  private todo: GemList<Issue>;

  private doing: GemList<Issue>;

  private done: GemList<Issue>;

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


