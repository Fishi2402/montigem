/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { JoinConnection } from '@src/demorec/JoinConnection';
import { JoinConnectionKind } from '@src/demorec/JoinConnectionKind';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { ChooseJoinConnectionKindComponent, config } from '@src/gui/connectionkind/ChooseJoinConnectionKindComponent';

import {
  Component,
  Output,
  EventEmitter,
} from '@angular/core';

import {Option} from "@umlp/input";

const newOption: Option = new Option("NEW");

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ChooseJoinConnectionKind extends ChooseJoinConnectionKindComponent {

  private connectionKindEntry :string
  private connectionKindOptions : Option[] = [newOption]
  private chosenKindNotLoaded : boolean = true

  @Output()
  public chosenKind: EventEmitter<JoinConnectionKind> = new EventEmitter<JoinConnectionKind>();

  public override init() {
    DemoRecManager.getJoinConnectionKindList().then(gemPromiseRes => {
      const jCKinds = gemPromiseRes;
      jCKinds.loadAll().then(() => {
        for (const jCKind of jCKinds) {
          let value = jCKind.getGemId() + "";
          jCKind.getNameAfterLoadPromise().then(name =>
              this.connectionKindOptions.push(new Option(value, name))
          )
          ;
        }
        this.connectionKindOptions = this.connectionKindOptions.slice();
      });
    });

    super.init();
  }

  public override getSelectJoinConnectionKindOptions() : Array<Option> {
    return this.connectionKindOptions
  }

  public override getSelectJoinConnectionKindEntry(): string {
    // This logic ensure that the type is chosen if the currentType is only loaded much later
    if(this.getCurrentKind() && this.chosenKindNotLoaded) {
      this.chosenKindNotLoaded = false;
      this.connectionKindEntry = this.getCurrentKind().getGemId()+""
    }
    return this.connectionKindEntry;
  }

  public override setSelectJoinConnectionKindEntry(value: string): void {
    this.connectionKindEntry = value;
    DemoRecManager.getJoinConnectionKindSure(Number(value)).then( kind =>
        this.chosenKind.emit(kind)
    )
  }

  public override getCreateJoinConnectionKindCondition(): boolean {
    return this.getSelectJoinConnectionKindEntry() === newOption.getValue();
  }

  public override addNewJoinConnectionKind() {
    return (newConnectionKind) => {
      newConnectionKind.getNameAfterLoadPromise().then(name => {
        this.connectionKindOptions.push(new Option(newConnectionKind.getGemId(), name))
      })
      this.setSelectJoinConnectionKindEntry(newConnectionKind.getGemId())
    }
  }

}
