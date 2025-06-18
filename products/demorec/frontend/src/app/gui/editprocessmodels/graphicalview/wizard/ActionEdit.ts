/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager'

import { ProcessStepType } from 'demorec/ProcessStepType';

import { ActionEditComponent,config} from 'gui/editprocessmodels/graphicalview/wizard/ActionEditComponent';

import { Option } from "@umlp/input";

import {
  Component,
  OnInit
} from '@angular/core';
import {Event} from "@umlp/common";

const noneOption: Option = new Option("NONE", "NONE");

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class ActionEdit extends ActionEditComponent implements OnInit {
  public selectedProcessStepEntry: string;
  public processStepOptions: Option[];

  public endEffectorEntry: string = "";
  public endEffectorOptions: Option[] = [noneOption];

  public joinConnectionEntry: string = "";
  public joinConnectionOptions: Option[] = [noneOption];

  public override ngOnInit(): void {
    super.ngOnInit();

    // Create viable process steps, which can be selected as underlying process step for the task
    if (this.getPmHasUnderlyingProcessCondition()) {
      this.fillProcessStepOptions();
    }
  }

  public override init(): void {
    super.init();

    // Create viable process steps, which can be selected as underlying process step for the task
    if (this.getPmHasUnderlyingProcessCondition()) {
      this.fillProcessStepOptions();
    }

    // Determine the task's current process step selection
    if ((this.getTask() !== null && this.getTask() !== undefined) &&
        (this.getTask().getUnderlyingProcessStep() !== null && this.getTask().getUnderlyingProcessStep() !== undefined)) {
      this.selectedProcessStepEntry = this.getTask().getUnderlyingProcessStep().getGemId() + "";
    }
    else {
      this.selectedProcessStepEntry = noneOption.getValue();
    }

    // Initialize endEffectorOptions
    DemoRecManager.getEndEffectorList().then(gemPromiseRes => {
      const endEffectors = gemPromiseRes;
      endEffectors.loadAll().then(() => {
        console.debug(endEffectors)
        this.endEffectorOptions = []
        for (const endEffector of endEffectors) {
          let gemId = endEffector.getGemId() + "";
          this.endEffectorOptions.push(new Option(gemId, endEffector.getName()));
        }
        this.endEffectorOptions = this.endEffectorOptions.slice();
      });
    });

    DemoRecManager.getJoinConnectionKindList().then(gemPromiseRes => {
      const joinConnectionKinds = gemPromiseRes;
      joinConnectionKinds.loadAll().then(() => {
        const options : Promise<Option>[] = []; //this.joinConnectionOptions = [newOption]
        for (const kind of joinConnectionKinds) {
          // Only add Options for this current pack!
          if(kind.getBatteryPackGem().getGemId() === this.getProcessModel().getUnderlyingProcess()?.getDeconstructionOf()?.getGemId()) {
            for (const joinConnection of kind.getJoinConnectionList()) {
              let name = joinConnection.getGemId() + "_" + kind.getJoinConnectionType().toString() + "(";
              name += kind.getJoinConnectionList().map(component => component.getNameAfterLoad()).join("-");
              name += ")";
              this.joinConnectionOptions.push(new Option(String(joinConnection.getGemId()), name));
            }
          }
        }
      });
    });

    console.error("JC Options Length:", this.joinConnectionOptions.length)
  }

  private fillProcessStepOptions(): void {
    if (!this.getPmHasUnderlyingProcessCondition()) {
      return;
    }
    this.processStepOptions = [noneOption];

    for (const processStep of this.getProcessModel().getUnderlyingProcess().getConsistsOfList()) {
      /*
        The process step is not viable for becoming the underlying process step of the task,
        if it is already the underlying process step of another task
       */
      let add = true;
      for (const task of this.getProcessModel().getSubTasksList()) {
        if (!task.equals(this.getTask())) {
          if (task.getUnderlyingProcessStep()?.equals(processStep)) {
            add = false;
          }
        }
      }
      if (add) {
        const value = processStep.getGemId() + "";
        const display = `Process Step with Index ${processStep.getIndex()} of Type "${ProcessStepType[processStep.getType()]}"`;
        this.processStepOptions.push(new Option(value, display));
      }
    }
    this.processStepOptions = this.processStepOptions.slice();
  }

  public override getPmHasUnderlyingProcessCondition (): boolean {
    return (this.getProcessModel().getUnderlyingProcess() !== null);
  }

  public override getUnderlyingProcessStepIsPresentCondition(): boolean {
    if (this.getTask() === null || this.getTask() === undefined) {
      return false;
    }
    return (this.getTask().getUnderlyingProcessStep() !== null || this.getTask().getUnderlyingProcessStep() !== undefined);
  }

  public override getSelectUnderlyingProcessStepOptions(): Array<Option> {
    return this.processStepOptions;
  }

  public override getSelectUnderlyingProcessStepEntry(): string {
    return this.selectedProcessStepEntry;
  }

  public override setSelectUnderlyingProcessStepEntry(value: string): void {
    this.selectedProcessStepEntry = value;
    if (this.selectedProcessStepEntry !== noneOption.getValue()) {
      console.error("Chose process step entry:" + this.selectedProcessStepEntry + "(none is '" + noneOption.getValue() + "'");
      DemoRecManager.getProcessStepSure(+this.selectedProcessStepEntry).then(processStep => {
        console.error("I got here")
        this.getTask().setUnderlyingProcessStep1(processStep);
        if (processStep.getDescription()) {
          this.getTask().setDescription(processStep.getDescription());
          this.setTaskDescriptionEntry(processStep.getDescription());
        }
        if (processStep.getTimeConsumption()) {
          this.getTask().setTimeConsumption(processStep.getTimeConsumption());
          this.setTaskDurationEntry(processStep.getTimeConsumption() + "");
        }
        if (processStep.getUses()) {
          this.getTask().setUses1(processStep.getUses());
          this.setTaskEndeffectorEntry(processStep.getUses().getGemId() + "");
        }
        if (processStep.getDeconstructs()) {
          this.getTask().setDeconstructs1(processStep.getDeconstructs());
          this.setTaskJoinConnectionEntry(processStep.getDeconstructs().getGemId() + "");
        }
      });
    }
    else {
      this.getTask().setUnderlyingProcessStepAbsent1();
    }

    // Create viable process steps, which can be selected as underlying process step for the task
    if (this.getPmHasUnderlyingProcessCondition()) {
      this.fillProcessStepOptions();
    }
  }

  public override getTaskEndeffectorOptions (): Array<Option> {
    return this.endEffectorOptions;
  }

  public override getTaskEndeffectorEntry (): string {
    if (this.endEffectorEntry) {
      return this.endEffectorEntry;
    }
    else {
      if(this.getTask()?.getUses()) {
        this.endEffectorEntry = this.getTask().getUses().getGemId() + "";
      }
      return this.endEffectorEntry;
    }
  }

  public override setTaskEndeffectorEntry(value: string): void {
    if (value === noneOption.getValue()) {
      this.getTask().setUsesPresent(false);
    }
    else {
      DemoRecManager.getEndEffectorSure(+value)
        .then((endEffector) => {
          this.getTask().setUses1(endEffector);
        })
    }
    this.endEffectorEntry = value;
  }

  public override getTaskJoinConnectionOptions(): Array<Option> {
    return this.joinConnectionOptions;
  }

  public override getTaskJoinConnectionEntry(): string {
    if (this.joinConnectionEntry && this.joinConnectionEntry != "") {
      return this.joinConnectionEntry;
    }
    else {
      if(this.getTask()?.getDeconstructsGem().isPresent()) {
        if(this.getTask()?.getDeconstructs()?.getGemId()) {
          this.joinConnectionEntry = this.getTask()?.getDeconstructs()?.getGemId() + "";
        }
      }
      return this.joinConnectionEntry;
    }
  }

  public override setTaskJoinConnectionEntry(value: string): void {
    this.joinConnectionEntry = value;

    if (this.getTaskJoinConnectionEntry()) {
      if (this.getTaskJoinConnectionEntry() === noneOption.getValue()) {
        this.getTask().setDeconstructsAbsent1();
      }
    }
  }

  public override createNewConnection (): Event<void> {
    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/wizard/CreateJoinConnectionForTask/" + this.getTask().getGemId()]);
  }
}


