/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { BatteryPack } from '@src/demorec/BatteryPack';
import { State } from '@src/demorec/State';

import { ComponentCreateComponent, config } from '@src/gui/component/ComponentCreateComponent';

import {
  Component,
  Output,
  EventEmitter,
} from '@angular/core';

import {BatteryPackService} from "@src/BatteryPackService";


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

  public constructor(protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
    })
  }

  public override ngOnInit(): void {
    DemoRecManager.demoRecComponentBuilder().then(gemPromiseRes => {
      this.component = gemPromiseRes;
      this.getComponent().setCondition(State.HEALTHY)
      this.getComponent().setBatteryPack1(this.currentPack)
    });

    this.init();
  }

  public override createNewComponent(): (arg: void) => void {
    return $ => {
      if(!this.getComponent()?.isBuildFinished()) {
        if(this.currentPack && !this.getComponent().getBatteryPack()) {
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

}
