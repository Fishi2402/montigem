/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { BatteryPack } from '@src/demorec/BatteryPack';
import { State } from '@src/demorec/State';

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
import { Photo } from '@src/demorec/Photo';
import {BatteryPackService} from "../../BatteryPackService";
import {BatteryPackOverview} from "../pack/BatteryPackOverview";


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ComponentCreate extends ComponentCreateComponent {

  protected currentPack : BatteryPack;

  @Output()
  public createComponent: EventEmitter<DemoRecComponent> = new EventEmitter<DemoRecComponent>();

  public constructor(router: Router, protected packService: BatteryPackService) {
    super(router);
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
    })
  }

  public override ngOnInit(): void {
    DemoRecManager.demoRecComponentBuilder().then(gemPromiseRes => {
      this.component = gemPromiseRes;
      this.getComponent().condition(State.HEALTHY)
    });

    this.init();
  }

  public override createNewComponent(event: any): void {

    if(this.getComponent()?.isBuildable() && !this.getComponent()?.isBuildFinished()) {
      if(this.currentPack) {
        this.getComponent().setBatteryPack1(this.currentPack)
      } else {
        console.warn("No Pack available for Component")
      }
      const component = this.getComponent().build()
      this.createComponent.emit(component)
      super.ngOnInit()
    }
  }

}
