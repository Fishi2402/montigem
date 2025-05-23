/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager'

import { Task } from 'demorec/Task'

import { DefineActionsComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/DefineActionsComponent';

import {
  Component,
} from '@angular/core';

import {Event} from "@umlp/common";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class DefineActions extends DefineActionsComponent {
  public defineANewAction: boolean = false;

  public override defineNewActionClicked(): Event<void> {
    return $ => this.defineANewAction = true;
  }

  public override createNewAction(): Event<Task> {
    return (newTask) => {
      newTask.build();

      // Add the new task to the process model
      this.getProcessModel().addSubTasks2(newTask);
      this.defineANewAction = false;
    }
  }

  public override getDefineNewActionButtonDisabled(): boolean {
    return this.getDefineANewActionCondition();
  }

  public override getDefineANewActionCondition(): boolean {
    return this.defineANewAction;
  }

  public override deleteAction (task: Task): Event<void> {
    return $ => {
      // Remove the task from the model
      this.getProcessModel().removeSubTasks2(task);

      // Remove the task as dependency
      for (const subTask of this.getProcessModel().getSubTasksList()) {
        subTask.removeDependsOn2(task);
      }

      // Finally: Delete the task at all
      DemoRecManager.deleteTask(task.getGemId());
    }
  }

  public override getModelHasActionsCondition(): boolean {
    if (this.getProcessModel()) {
      return (this.getProcessModel().sizeSubTaskss() > 0);
    }
    else {
      return false;
    }
  }

  public override back() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineProcessModel/" + this.processModel.getGemId()]);
  }

  public override next() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineDependencies/" + this.processModel.getGemId()]);
  }
}
