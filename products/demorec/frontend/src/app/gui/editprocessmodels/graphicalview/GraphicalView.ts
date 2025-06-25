/* (c) https://github.com/MontiCore/monticore */
import { Event } from '@umlp/common';

import { DemoRecManager } from 'demorec/DemoRecManager'

import { Task } from 'demorec/Task'

import { GraphicalViewComponent,config} from 'gui/editprocessmodels/graphicalview/GraphicalViewComponent';

import {
  Component,
  OnInit
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class GraphicalView extends GraphicalViewComponent implements OnInit {
  public override ngOnInit(): void {
    super.ngOnInit();

    requestAnimationFrame(() => {
      document.querySelector('.graphicalViewButton > button.gem-button').classList.add('clicked');
      document.querySelector('.textualViewButton > button.gem-button').classList.remove('clicked');
    });
  }

  public override getViewActionsLabel(): string {
    return "Actions (" + this.getPm()?.sizeSubTaskss() + ")";
  }

  public override getDependsOnList (task: Task): string {
    const dependsOnsNames = [];
    for (const dependsOnTask of task.getDependsOnList()) {
      dependsOnsNames.push(dependsOnTask.getName());
    }
    if (dependsOnsNames.length === 0) {
      return "None";
    }
    else {
      return dependsOnsNames.toString();
    }
  }

  public override getModelHasTasksCondition (): boolean {
    return (this.getPm()?.sizeSubTaskss() > 0);
  }

  public override goToUnderlyingProcess(): (arg: void) => void {
    return $ => this._router.navigate(["gui/process/ProcessOverview/" + this.getPm().getUnderlyingProcess().getGemId()]);
  }

  public override goToUnderlyingProcessStep (task: Task): (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/ViewUnderlyingProcessStepDetails/" + task.getGemId()]);
  }

  public override editProcessModel() : (arg: void) => void {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/DefineProcessModel/" + this.getPm().getGemId()]);
  }

  public override deleteProcessModel (): Event<void> {
    return $ => {
      DemoRecManager.deleteProcessModel(this.getPm().getGemId());
      this._router.navigate(["gui/editprocessmodels/ProcessModels/"]);
    }
  }
}
