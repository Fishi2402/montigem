/* (c) https://github.com/MontiCore/monticore */
import { ActionCreateComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/ActionCreateComponent';

import { DemoRecManager } from 'demorec/DemoRecManager';

import { Task } from 'demorec/Task'

import {
  Component,
  Output,
  EventEmitter,
  OnInit
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

export class ActionCreate extends ActionCreateComponent implements OnInit {
  @Output()
  public newTaskEvent: EventEmitter<Task> = new EventEmitter<Task>();

  public override ngOnInit(): void {
    super.ngOnInit();

    DemoRecManager.taskBuilder().then(gemPromiseRes => {
      this.task = gemPromiseRes;
      this.task.setProcessModel1(this.getProcessModel());
      this.task.setName("");
      this.task.setDescription("");
      this.task.setTimeConsumption(0.0);
      this.task.setUses1(null);
      this.task.setDeconstructs1(null);
    });
  }

  public override getActionCreateButtonDisabled (): boolean {
    return !this.isTaskBuildable();
  }

  private isTaskBuildable(): boolean {
    if (this.getTask().getName() && (this.getTask().getUses() !== null)) {
      // If the mandatory attributes are set, the task is good to go
      return true;
    }

    return false;
  }

  public override createNewAction (): (arg: void) => void {
    return $ => {
      if(this.isTaskBuildable()) {
        this.newTaskEvent.emit(this.getTask().build());
      } else {
        console.error("New Task is not buildable");
      }
    }
  }
}


