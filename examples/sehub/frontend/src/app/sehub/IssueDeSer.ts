/* (c) https://github.com/MontiCore/monticore */

import {IssueDeSerTOP} from '@src/sehub/IssueDeSerTOP';
import {IssueState} from '@src/sehub/IssueState';
import {Issue} from '@src/sehub/Issue';

export class IssueDeSer extends IssueDeSerTOP {

  protected override serializeState(obj: Issue, printer: any): void {
    printer["state"] = IssueState[obj.getState()];
  }
}
