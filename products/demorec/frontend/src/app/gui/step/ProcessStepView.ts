/* (c) https://github.com/MontiCore/monticore */
import { ProcessStepType } from 'demorec/ProcessStepType';

import { JoinConnectionType } from 'demorec/JoinConnectionType';

import { ProcessStepViewComponent, config } from'gui/step/ProcessStepViewComponent';

import {
  Component,
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class ProcessStepView extends ProcessStepViewComponent {
  public override getIndex (): string {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      return this.getProcessStep().getIndex() + "";
    }
    return "None";
  }

  public override getType(): string {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      return ProcessStepType[this.getProcessStep().getType()] + "";
    }
    return "None";
  }

  public override getEndEffectorName (): string {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      if (this.getProcessStep().getUses() !== null && this.getProcessStep().getUses() !== undefined) {
        return this.getProcessStep().getUses().getName();
      }
      return "None";
    }
    return "None";
  }

  public override getHasJoinConnectionCondition (): boolean {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      return (this.getProcessStep().getDeconstructs() != null && this.getProcessStep().getDeconstructs() != undefined);
    }
    return false;
  }

  public override getJoinConnectionLabelText (): string {
    if (this.getHasJoinConnectionCondition()) {
      if (this.getProcessStep().getDeconstructs().getJoinConnectionKind() !== null && this.getProcessStep().getDeconstructs().getJoinConnectionKind() !== undefined) {
        return "Dissolved Components of Join Connection of Type '"
            + JoinConnectionType[this.getProcessStep().getDeconstructs().getJoinConnectionKind().getJoinConnectionType()]
            + "':";
      }
      return "Dissolved Components of Join Connection";
    }
    return "Dissolved Components of Join Connection";
  }

  public override getJoinConnectionComponents (): string {
    if (this.getHasJoinConnectionCondition()) {
      if (this.getProcessStep().getDeconstructs().getJoinConnectionKind() !== null && this.getProcessStep().getDeconstructs().getJoinConnectionKind() !== undefined) {
        if (this.getProcessStep().getDeconstructs().getJoinConnectionKind().getConnectsList() !== null && this.getProcessStep().getDeconstructs().getJoinConnectionKind().getConnectsList() !== undefined) {
          let componentNames = [];
          for (const component of this.getProcessStep().getDeconstructs().getJoinConnectionKind().getConnectsList()) {
            componentNames.push(component.getName());
          }
          return componentNames.toString();
        }
        return "None";
      }
      return "None";
    }
    return "None";
  }

  public override getDescription (): string {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      if (this.getProcessStep().isPresentDescription() && this.getProcessStep().getDescription() !== null && this.getProcessStep().getDescription() !== undefined) {
        if (this.getProcessStep().getDescription().length > 0) {
          return this.getProcessStep().getDescription();
        }
        return "None";
      }
      return "None";
    }
    return "None";
  }

  public override getDuration (): string {
    if (this.getProcessStep() !== null && this.getProcessStep() !== undefined) {
      if (this.getProcessStep().getTimeConsumption() !== null && this.getProcessStep().getTimeConsumption() !== undefined) {
        return this.secondsToMinutes(this.getProcessStep().getTimeConsumption()) + " (" + this.getProcessStep().getTimeConsumption() + " seconds)";
      }
      return "None";
    }
    return "None";
  }

  private secondsToMinutes(seconds): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes} minute(s) and ${remainingSeconds} second(s)`;
  }
}


