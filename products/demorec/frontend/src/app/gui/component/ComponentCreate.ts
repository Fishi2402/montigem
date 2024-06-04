/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ComponentKind } from '@src/demorec/ComponentKind';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { ComponentCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentCreateComponent';
import { Option } from '@umlp/input';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';


const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ComponentCreate extends ComponentCreateComponent {

  @Output()
  public createComponent: EventEmitter<DemoRecComponent> = new EventEmitter<DemoRecComponent>();


  public kindEntry: string = "";
  public kindOptions: Option[] = [newOption];

  public override ngOnInit(): void {
    super.ngOnInit()

    DemoRecManager.getComponentKindList().then(gemPromiseRes => {
      const kinds = gemPromiseRes;
      kinds.loadAll().then(() => {
        console.error(kinds)
        for (const kind of kinds) {
          let value = kind.getGemId() + "";
          this.kindOptions.push({value: value, display: kind.getName()});
        }
        this.kindOptions = this.kindOptions.slice();
      });
    });
  }

  public override getSelectComponentKindOptions(): Array<Option> {
    return this.kindOptions;
  }

  public override getSelectComponentKindEntry(): string {
    return this.kindEntry;
  }

  public override setSelectComponentKindEntry(value: string): void {
    this.kindEntry = value;
  }

  public override createNewComponent(): (arg: void) => void {
    return $ => {
      /*if(this.getComponent()?.isBuildFinished()) {
        this.createComponent.emit(this.getComponent())
      }*/
      let kind : Promise<ComponentKind>
      if(this.getSelectComponentKindEntry() != newOption.value) {
        kind=DemoRecManager.getComponentKindSure(+this.getSelectComponentKindEntry())
      } else {
        this.getComponentKind().build()
        kind=new Promise((resolve, reject) => {
          if(this.getComponentKind().isBuildFinished()) {
            resolve(this.getComponentKind())
          } else {
            reject("Component kind could not be built.")
          }
        })
      }

      kind.then(k => {
        this.getComponent().isA(k)
        if(this.getComponent()?.isBuildable() && !this.getComponent()?.isBuildFinished()) {
          const component = this.getComponent().build()
          this.createComponent.emit(component)
          super.ngOnInit()
        }
      })
    }
  }

  public override getCreateComponentKindCondition (): boolean {
    return this.getSelectComponentKindEntry()==newOption.value
  }
}


