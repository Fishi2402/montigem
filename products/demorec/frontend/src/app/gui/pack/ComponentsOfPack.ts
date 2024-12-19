/* (c) https://github.com/MontiCore/monticore */
import { ComponentKind } from '@src/demorec/ComponentKind';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { ComponentsOfPackComponent ,config} from '@src/gui/pack/ComponentsOfPackComponent';
import { Event } from '@umlp/common'

import {
  Component,
} from '@angular/core';

import {BatteryPackService} from "@src/BatteryPackService";
import {JoinConnectionKind} from "@src/demorec/JoinConnectionKind";
import {JoinConnection} from "@src/demorec/JoinConnection";


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentsOfPack extends ComponentsOfPackComponent {

  protected lastPack : BatteryPack

  public constructor( protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(pack => {
      if(pack) {
        this.lastPack = pack
      }
    })
  }

  public override navigateToComponent(component: DemoRecComponent): Event {
    return () => {
      this._router.navigate(["gui/component/ComponentEditPage", component.getGemId()]);
    }
  }

  public override navigateToKind(kind: ComponentKind): Event  {
    return () => {
      if (kind.getDemoRecComponentList()) {
        this._router.navigate(["gui/kind/EditComponentKindPage", this.getPack().getGemId(), kind.getGemId()])
      }
    }
  }

  public override navigateToConnection(connection: JoinConnection): Event  {
    return () => {
      this._router.navigate(["gui/connectionkind/JoinConnectionKindEditPage", connection.getGemId()])
    }
  }

  public override getConnectionKindName(connKind: JoinConnectionKind): string {
    return connKind.getNameAfterLoad();
  }

  public override getConnectionName(conn: JoinConnection): string {
    return conn.getGemId().toString();
  }

  public override navigateToPackEdit(): Event {
    return () => this._router.navigate(["gui/pack/PackEditPage/", this.getPack().getGemId()])
  }
}


