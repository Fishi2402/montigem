/* (c) https://github.com/MontiCore/monticore */
import { Task } from 'demorec/Task'

import { DependenciesEditComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/DependenciesEditComponent';

import { Option } from '@umlp/input'

import { Event } from "@umlp/common";

import {
  Component,
  OnInit
} from '@angular/core';

const noneOption: Option = new Option("NONE", "NONE");

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class DependenciesEdit extends DependenciesEditComponent implements OnInit {
  taskOptions : Option[] = [noneOption];
  selectedDependenciesNames : string[] = [];
  selectedTaskEntry : string = "";
  warningMessage: string = "";

  public override ngOnInit() {
    super.ngOnInit();

    // Fill task options into drop-down menu
    for (const otherSubtask of this.getTask().getProcessModel().getSubTasksList()) {
      if (!this.getTask().equals(otherSubtask)) {
        if (!this.getTask().containsDependsOn(otherSubtask)) {
          this.pushTaskOption(otherSubtask);
        }
      }
    }

    // Collect names of dependency tasks for displaying them as selected dependencies
    for (const dependency of this.getTask().getDependsOnList()) {
      this.selectedDependenciesNames.push(dependency.getName());
    }
  }

  private pushTaskOption(task: Task): void {
    this.taskOptions.push(new Option(task.getGemId() + "", task.getName()));
  }

  private removeTaskOption(task: Task): void {
    let index = -1;
    for (const option of this.taskOptions) {
      if (+option.getValue() === task.getGemId()) {
        index = this.taskOptions.indexOf(option);
      }
    }
    if (index !== -1) {
      this.taskOptions.splice(index, 1);
    }
  }

  public override getSelectDependsOnDisabled (): boolean {
    return this.taskOptions.length === 0;
  }

  public override getAddDependsOnButtonDisabled (): boolean {
    return this.getSelectDependsOnDisabled();
  }

  public override getSelectDependsOnOptions (): Array<Option> {
    return this.taskOptions;
  }

  public override setSelectDependsOnEntry (selectedTaskGemId: string): void {
    this.selectedTaskEntry = selectedTaskGemId;
  }

  public override getSelectDependsOnEntry (): string {
    return this.selectedTaskEntry;
  }

  public override getWarning (): string {
    return this.warningMessage;
  }

  public override addDependency(): Event<void> {
    return $ => {
      if (this.selectedTaskEntry === noneOption.getValue()) {
        // Remove all dependencies, if the user wants none of them
        this.selectedDependenciesNames = [];
      }
      else {
        for (const task of this.getTask().getProcessModel().getSubTasksList()) {
          if (task.getGemId() === +this.selectedTaskEntry) {
            this.addSelectedDependencyTask(task);
          }
        }
      }
    }
  }

  public override getSelectedDependencies(): Array<string> {
    return this.selectedDependenciesNames;
  }

  private addSelectedDependencyTask(dependency: Task) : void {
    if (!this.checkForCyclicDependency(dependency)) {
      this.getTask().addDependsOn2(dependency);
      this.selectedDependenciesNames.push(dependency.getName());
      this.removeTaskOption(dependency);
      this.warningMessage = "";
    }
    else {
      this.warningMessage = "Cannot add dependency, because it would create a circular dependency between the tasks."
    }
  }

  private checkForCyclicDependency(dependency: Task) : boolean {
    return dependency.getDependsOnList().includes(this.getTask());
  }

  private removeSelectedDependencyTask(dependency: Task) : void {
    // Remove the dependency from the list of dependencies of the task itself
    this.getTask().removeDependsOn2(dependency);

    // Remove the dependency from the selected, displayed options
    const indexOfDependencyName = this.selectedDependenciesNames.indexOf(dependency.getName());
    if (indexOfDependencyName !== -1) {
      this.selectedDependenciesNames.splice(indexOfDependencyName, 1);
    }

    // Remove the dependency from the drop-down-displayed options
    this.pushTaskOption(dependency);
  }

  public override removeSelectedDependency(taskName: string): Event<void> {
    return $ => {
      for (const dependency of this.getTask().getDependsOnList()) {
        if (dependency.getName() === taskName) {
          this.removeSelectedDependencyTask(dependency);
        }
      }
    }
  }
}


