/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager';

import { CommandManager } from '@umlp/common';

import { CreateInitialProcessModel } from 'demorec/commands/CreateInitialProcessModel';

import { CreateNewProcessModelComponent,config} from 'gui/editprocessmodels/create/CreateNewProcessModelComponent';

import {
  Component,
  OnInit,
} from '@angular/core';
import {Option} from "@umlp/input";
import {Event} from "@umlp/common";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class CreateNewProcessModel extends CreateNewProcessModelComponent implements OnInit {
  public selectedProcessEntry: string = "";
  public processOptions: Option[] = [];

  public override ngOnInit() {
    super.ngOnInit();

    DemoRecManager.getProcessList().then(gemPromiseRes => {
      const processes = gemPromiseRes;
      processes.loadAll().then(() => {
        for (const p of processes) {
          if (p.getProcessModel() === null) {
            let value = p.getGemId() + "";
            this.processOptions.push(new Option(value, `Disassembly of Battery Pack "${p.getDeconstructionOf()?.getName()}"`));
          }
        }
        this.processOptions = this.processOptions.slice();
      });
    });
  }

  private removeProcessOption(index: string): void {
    this.processOptions = this.processOptions.filter(option => (option.getValue() !== index));
  }

  public override getSelectUnderlyingProcessOptions (): Array<Option> {
    return this.processOptions;
  }

  public override getSelectUnderlyingProcessEntry (): string {
    return this.selectedProcessEntry;
  }

  public override setSelectUnderlyingProcessEntry (newEntry: string): void {
    this.selectedProcessEntry = newEntry;
  }

  public override createNewProcessModel(): Event<void> {
    return $ => {
      DemoRecManager.getProcessSure(+this.selectedProcessEntry).then((process) => {
        // If the process has no process model, create an initial one
        CommandManager.executeCommand(new CreateInitialProcessModel(process.getGemId())).then(res => {
          const outcome = res.asSimpleResult();

          if (outcome.isSuccessful()) {
            console.warn("Created an initial process model with GemID " + outcome.get()
                + " for the process with GemID "+ process.getGemId()
                + " in the backend side, as the process previously lacked a process model");
            this._router.navigate(["gui/editprocessmodels/edit/GraphicalView/" + outcome.get().getGemId()]);
          }
          else {
            console.error("Failed to create an initial process model for the process with GemID "
                + process.getGemId() + " in the backend side");
          }
        }).catch(reason => {
          console.error("Failed to create an initial process model for the process with GemID "
              + process.getGemId() + " in the backend side for the following reason: " + reason);
        });
      });
    }
  }

  public override getAProcessHasNoProcessModelCondition(): boolean {
    return this.processOptions.length > 0;
  }

  public override navigateToProcessModelLandingPage (): Event<void> {
    return $ => this._router.navigate(["gui/editprocessmodels/ProcessModels"]);
  }
}
