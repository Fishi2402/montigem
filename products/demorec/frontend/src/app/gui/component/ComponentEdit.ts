/* (c) https://github.com/MontiCore/monticore */
import { State } from '@src/demorec/State';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { ComponentKind } from '@src/demorec/ComponentKind';
import { Photo } from '@src/demorec/Photo';
import { DemoRecManager } from '@src/demorec/DemoRecManager';
import {GemTextInputValidity, Option} from '@umlp/input';

import { ComponentEditComponent, config } from '@src/gui/component/ComponentEditComponent';

import {
  Component,
} from '@angular/core';
import {GemList} from "@umlp/common";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentEdit extends ComponentEditComponent {

  conditionOptions : Option[] = []
  allComponentKinds : ComponentKind[]= []

  public init(): void {
    this.conditionOptions = []
    Object.keys(State).filter(s => !isNaN(+s)).forEach(key => {
      this.conditionOptions.push({value: key, display: State[key]});
    })

    DemoRecManager.getComponentKindList()
        .then(l => {return l.loadAll()})
        .then( l => this.allComponentKinds = l)

    super.init();
  }

  public editComponentKind(): (arg: void) => void {

    return $ => {
      let packId;
      if(this.getComponent() instanceof BatteryPack) {
        packId = this.getComponent().getGemId()
      } else {
        packId = this.getComponent().getBatteryPack().getGemId()
      }
      this._router.navigateByUrl("gui/kind/EditComponentKindPage/"+packId
          +"/"+this.getComponent().getIsA().getGemId());
    }
  }

  public override addPhoto() : (arg : Photo) => void {
    return (photo : Photo) => {
      this.getComponent().addPhoto2(photo)
    }
  }

  public override changeToKind() : (arg : ComponentKind) => void  {
    return (kind : ComponentKind) => {
      this.getComponent().setIsA1(kind)
    }
  }

  // Select condition of connection
  public override getConditionOptions (): Array<Option> {
    return this.conditionOptions;
  }

  public override getConditionEntry (): string {
    return this.getComponent()?.getCondition()?.toString()
  }

  public override setConditionEntry (value: string): void {
    Object.keys(State).filter((v) => isNaN(Number(v))).forEach((key, index) => {
      if (+value === index) {
        this.getComponent()?.setCondition(State[key])
      }
    })
  }

  public override checkComponentName() :GemTextInputValidity {
    if(this.getComponent()) {
      if(this.getComponent().getName()) {
        return {status: "Success"}
      }
      // TODO check if the name already exists
      return {status: "Error", message: "Please enter a name."}
    } else {
      return {status: "Warning", message: "Wait for initialization."}
    }
  }

  public override getAvailableKinds(): ComponentKind[] {
    if(this.getComponent()) {
      if(this.getComponent().getBatteryPack()) {
        return this.getComponent().getBatteryPack().getAllKindsList()
      } else {
        return this.allComponentKinds
      }
    }
    return []
  }
}
