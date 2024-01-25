/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { ComponentCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentCreateComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

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

  public override getDeconstructibleChecked (): boolean {
    return this.getComponent()?.isIsDeconstructible()
  }

  public override setDeconstructibleChecked (value: boolean): void {
    this.getComponent().isDeconstructible(value)
  }

  public override createNewComponent(event: any): void {
    /*if(this.getComponent()?.isBuildFinished()) {
      this.createComponent.emit(this.getComponent())
    }*/
    if(this.getComponent()?.isBuildable() && !this.getComponent()?.isBuildFinished()) {
      const component = this.getComponent().build()
      this.createComponent.emit(component)
      super.ngOnInit()
    }
  }
}


