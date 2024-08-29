/* (c) https://github.com/MontiCore/monticore */

import { ComponentKind } from '@src/demorec/ComponentKind';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { CreateComponentKindComponent, config } from '@src/gui/kind/CreateComponentKindComponent';

import {
  Component,
  Output,
  EventEmitter
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
export class CreateComponentKind extends CreateComponentKindComponent {

  protected currentPack : BatteryPack;

  @Output()
  public newKind : EventEmitter<ComponentKind> = new EventEmitter<ComponentKind>()

  public constructor(protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
    })
  }

  public override createNewComponentKind(): (arg: void) => void {
    return $ => {
      if(!this.getKind().isBuildFinished()) {
        if(this.currentPack) {
          this.getKind().setBatteryPack1(this.currentPack)
        }
        if(this.getKind().isBuildable()) {
          this.newKind.emit(this.getKind().build())
        }
      }
    }
  }

}


