/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from 'demorec/DemoRecManager';

import { ComponentKind } from 'demorec/ComponentKind';
import {GemTextInputValidity, Option} from '@umlp/input';

import { ChooseComponentKindComponent, config } from 'gui/kind/ChooseComponentKindComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import {ComponentEdit} from "../component/ComponentEdit";

const newOption: Option = new Option("NEW");

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ChooseComponentKind extends ChooseComponentKindComponent {

  public kindEntry: string = "";
  public kindOptionsHash:{value: Option[], hash: string} = {value: [], hash: ""}
  private chosenKindNotLoaded: boolean = true

  @Output()
  public chosenKind : EventEmitter<ComponentKind> = new EventEmitter<ComponentKind>()

  public init(): void {
    this.chosenKindNotLoaded = true;
    this.kindEntry = "";

    // Copied from initAvailableKinds
    DemoRecManager.getComponentKindList().then(tmp_$ => {
      tmp_$.loadAll().then(tmp_$1 => {
        if (!this._availableKinds) {
          this._availableKinds = tmp_$1;
        }
      });
    });

    super.init();
  }

  public override getCreateComponentKindCondition (): boolean {
    return this.getSelectComponentKindEntry()===newOption.getValue()
  }

  public override getSelectComponentKindOptions(): Array<Option> {
    if(this.getAvailableKinds()) {
      let opts = []
      for (const kind of this.getAvailableKinds()) {
        let value = kind.getGemId() + "";
        opts.push(new Option(value, kind.getName()));
      }
      opts = [newOption].concat(opts.sort((a,b) => {return a.getValue().localeCompare(b.getValue())}).slice());
      const hash = JSON.stringify(opts)
      if(this.kindOptionsHash.hash != hash) {
        this.kindOptionsHash.hash = hash
        this.kindOptionsHash.value = opts
        this.kindEntry = this.getCurrentKind() ? this.getCurrentKind().getGemId() + "" : newOption.getValue()
      }
    }
    return this.kindOptionsHash.value;
  }

  public override getSelectComponentKindEntry(): string {
    return this.kindEntry;
  }

  public override setSelectComponentKindEntry(value: string): void {
    this.kindEntry = value;
    if(value != newOption.getValue()) {
      DemoRecManager.getComponentKindSure(+this.getSelectComponentKindEntry()).then(kind => this.chosenKind.emit(kind))
    }
  }

  // TODO
  public override addNewComponentKind(): (arg : ComponentKind) => void {
    return (kind : ComponentKind) => {
      this.kindOptionsHash.value.push(new Option(kind.getGemId()+"", kind.getName()))
      this.setSelectComponentKindEntry(kind.getGemId()+"")
    }
  }

  public override checkKindChoice() : GemTextInputValidity {
    if(this.kindEntry) {
      if(this.kindEntry === newOption.getValue()) {
        return new GemTextInputValidity("Error", "Please save the new component kind or choose another one.");
      }
      return new GemTextInputValidity("Success");
    }
    return new GemTextInputValidity("Error", "Please enter a valid component kind.");
  }

}


