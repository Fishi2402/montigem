/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { JoinConnection } from 'demorec/JoinConnection';
import { State } from 'demorec/State';

import { JoinConnectionCreateCompleteComponent, config } from '../../../../build/frontendGen/build/appgui/gui/joinconnection/JoinConnectionCreateCompleteComponent';

import {
  Component,
  Output,
  EventEmitter,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import {GemTextInputValidity, Option} from "@umlp/input";
import {JoinConnectionKind} from "demorec/JoinConnectionKind";

const newOption: Option = new Option("NEW");
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class JoinConnectionCreateComplete extends JoinConnectionCreateCompleteComponent {
  @Output()
  public createJoinConnection: EventEmitter<JoinConnection> = new EventEmitter<JoinConnection>();

  public joinConnectionKindEntry: string = "";
  public joinConnectionKindOptions: Option[] = [newOption];

  public override ngOnInit() {
    DemoRecManager.getJoinConnectionKindList().then(gemPromiseRes => {
      const joinConnectionKinds = gemPromiseRes;
      joinConnectionKinds.loadAll().then(() => {
        const options : Promise<Option>[] = []; //this.joinConnectionOptions = [newOption]
        for (const kind of joinConnectionKinds) {

          // Only add Options for this current pack!
          if(kind.getBatteryPackGem().getGemId() === this.getProcessModel().getUnderlyingProcess().getDeconstructionOf().getGemId()) {
            options.push(kind.getNameAfterLoadPromise().then(name => {
              return (new Option(String(kind.getGemId()), name))
            }))
          }
        }
        // Get newest JoinConnectionKinds -> Ordered by GemID Descending
        Promise.all(options).then(results => {
          this.joinConnectionKindOptions = this.joinConnectionKindOptions.concat(results.sort((l,r) => Number(r.getValue())-Number(l.getValue())))
        })
      });
    });

    DemoRecManager.joinConnectionBuilder().then(gemPromiseRes => {
      this.connection = gemPromiseRes;
      this.connection.setCondition(State.HEALTHY)
    });
    this.init();
  }

  public override createNewConnection(): (arg: void) => void  {
    return $ => {
      if(this.getConnection().isBuildable()) {
        console.debug("New Connection is buildable")
        const joinConnection = this.getConnection().build()
        this.createJoinConnection.emit(joinConnection)

      } else {
        console.debug("New Connection is not buildable")
        this.getConnection().build()
      }
    }
  }

  public override getCreateNewJoinConnectionKindCondition() :boolean {
    return (this.getSelectJoinConnectionKindEntry() === "NEW");
  }

  // Select Join Connection
  public override getSelectJoinConnectionKindOptions(): Array<Option> {
    return this.joinConnectionKindOptions;
  }

  public override getSelectJoinConnectionKindEntry(): string {
    return this.joinConnectionKindEntry;
  }

  public override setSelectJoinConnectionKindEntry(value: string): void {
    this.joinConnectionKindEntry = value;
  }

  public override addNewJoinConnectionKind(): (arg: JoinConnectionKind) => void {
    return (connectionKind: JoinConnectionKind) => {
      connectionKind.getNameAfterLoadPromise().then(name => {
        this.joinConnectionKindOptions.push(new Option(connectionKind.getGemId()+"", name))
        this.setSelectJoinConnectionKindEntry(String(connectionKind.getGemId()))
      })
    }
  }

  public override checkJoinConnectionKind(): GemTextInputValidity {
    if(this.joinConnectionKindEntry) {
      if(this.joinConnectionKindEntry === newOption.getValue()) {
        return new GemTextInputValidity("Error", "Please Save the new Connection.");
      }
      return new GemTextInputValidity("Success", "Success");
      ;
    } else {
      return new GemTextInputValidity("Error","Please choose an existing Connection or create a new one.")
    }
  }

}
