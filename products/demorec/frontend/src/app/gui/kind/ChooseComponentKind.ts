/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ComponentKind } from '@src/demorec/ComponentKind';
import {GemTextInputValidity, Option} from '@umlp/input';

import { ChooseComponentKindComponent, config } from '../../../../build/frontendGen/build/appgui/gui/kind/ChooseComponentKindComponent';

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

const newOption: Option = { value: "NEW", display: "NEW" };

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
  public kindOptions: Option[] = [newOption];
  private chosenKindNotLoaded: boolean = true

  @Output()
  public chosenKind : EventEmitter<ComponentKind> = new EventEmitter<ComponentKind>()

  public constructor() {
    super();

    this._router.events.subscribe((val) => {
      if(val instanceof NavigationEnd) {
        if(this._route.snapshot.paramMap.get("currentKindRoutingId") != null) {
          this.currentKindRoutingId = +this._route.snapshot.paramMap.get("currentKindRoutingId");
          this.ngOnInit()
          this.chosenKindNotLoaded=true
          this.kindEntry = ""
        }
      }
    });
  }

  public override ngOnInit() {
    if(this.currentKindRoutingId != null && ((this.currentKind == null) || (this.currentKind.getGemId() != this.currentKindRoutingId))) {
      DemoRecManager.getComponentKindSure(this.currentKindRoutingId).then(gemPromiseRes => {
        this.currentKind = gemPromiseRes;
      });
    }

    DemoRecManager.getComponentKindList().then(gemPromiseRes => {
      const kinds = gemPromiseRes;
      kinds.loadAll().then(() => {
        for (const kind of kinds) {
          let value = kind.getGemId() + "";
          this.kindOptions.push({value: value, display: kind.getName()});
        }
        this.kindOptions = this.kindOptions.slice();
        this.kindEntry = this.getCurrentKind()? this.getCurrentKind().getGemId()+"" : newOption.value
      });
    });

    this.init();
  }

  public override getCreateComponentKindCondition (): boolean {
    return this.getSelectComponentKindEntry()===newOption.value
  }

  public override getSelectComponentKindOptions(): Array<Option> {
    return this.kindOptions;
  }

  public override getSelectComponentKindEntry(): string {
    if(this.kindEntry===newOption.value) {
      return this.kindEntry;
    }
    if(this.getCurrentKind() && (this.chosenKindNotLoaded || ((this.getCurrentKind().getGemId()+"") != this.kindEntry)) ) {
      this.chosenKindNotLoaded = false;
      this.kindEntry = this.getCurrentKind().getGemId()+""
    }
    return this.kindEntry;
  }

  public override setSelectComponentKindEntry(value: string): void {
    this.kindEntry = value;
    if(value != newOption.value) {
      DemoRecManager.getComponentKindSure(+this.getSelectComponentKindEntry()).then(kind => this.chosenKind.emit(kind))
    }
  }

  public override addNewComponentKind(): (arg : ComponentKind) => void {
    return (kind : ComponentKind) => {
      this.kindOptions.push({value: kind.getGemId()+"", display: kind.getName()})
      this.setSelectComponentKindEntry(kind.getGemId()+"")
    }
  }

  public override checkKindChoice() : GemTextInputValidity {
    if(this.kindEntry) {
      if(this.kindEntry === newOption.value) {
        return {status: "Error", message: "Please save the new component kind or choose another one."}
      }
      return {status: "Success"}
    }
    return {status: "Error", message: "Please enter a valid component kind."}
  }

}


