/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';
import { State } from '@src/demorec/State';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';
import { ComponentKind } from '@src/demorec/ComponentKind';
import { Photo } from '@src/demorec/Photo';
import { Option } from '@umlp/input';

import { ComponentEditComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentEditComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef
} from '@angular/core';

import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentEdit extends ComponentEditComponent {

  public kindEntry: string = "";
  public kindOptions: Option[] = [newOption];

  conditionOptions : Option[] = []

  public init(): void {
    this.conditionOptions = []
    Object.keys(State).filter(s => !isNaN(+s)).forEach(key => {
      this.conditionOptions.push({value: key, display: State[key]});
    })

    DemoRecManager.getComponentKindList().then(gemPromiseRes => {
      const kinds = gemPromiseRes;
      kinds.loadAll().then(() => {
        for (const kind of kinds) {
          let value = kind.getGemId() + "";
          this.kindOptions.push({value: value, display: kind.getName()});
        }
        this.kindOptions = this.kindOptions.slice();
      });
    });

    super.init();
  }

  public editComponentKind(): (arg: void) => void {
    return $ => this._router.navigateByUrl("gui/kind/EditComponentKindPage/"+this.getComponent().getIsA().getGemId());
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

}


