/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { ComponentKind } from '@src/demorec/ComponentKind';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ComponentsOfPackComponent ,config} from '@src/gui/pack/ComponentsOfPackComponent';
import { GemTreeviewEntry } from '@umlp/basic';

import {
  Component,
} from '@angular/core';

import {BatteryPackService} from "@src/BatteryPackService";

const unroutable : string = "Not clickable yet."

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class ComponentsOfPack extends ComponentsOfPackComponent {

  protected treeComponentItems: { value: GemTreeviewEntry[], hash: string }
      = { value: [], hash: '' };

  protected treeConnectionItems: { value: GemTreeviewEntry[], hash: string }
      = { value: [], hash: '' };

  protected alreadyUpdatePack : boolean = false
  protected lastPack : BatteryPack

  public constructor( protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(pack => {
      if(pack) {
        this.lastPack = pack
      }
    })
  }

  public override navigateToComponent() : (arg) => void  {
    return (entry) => {
      if(entry.children) {
        this._router.navigate(["gui/kind/EditComponentKindPage/"+this.getPack().getGemId()+"/"+entry.value])
      } else if(entry.value != unroutable) {
        this._router.navigate(["gui/component/ComponentEditPage/"+entry.value])
      }
    }
  }

  public override getComponentsItems(): GemTreeviewEntry[] {
    if(!this.getPack()) {
      return []
    }
    if(this.lastPack != this.getPack()) {
      this.packService.updateBatteryPack(this.getPack())
    }
    const entries =[]
    const kindToComponents : Map<ComponentKind, DemoRecComponent[]> = new Map()

    this.getPack().getAllComponentsList().forEach(c => {
      if(c.getIsA()) {
        if(kindToComponents.has(c.getIsA())) {
          kindToComponents.get(c.getIsA()).push(c)
        } else {
          kindToComponents.set(c.getIsA(), [c])
        }
      }
    });

    const keys = [...kindToComponents.keys()];
    if(keys.length>0) {
      keys.sort((l,r) => l.getName().localeCompare(r.getName())).forEach(
          kind => {
            const entry = new GemTreeviewEntry();
            entry.name=kind.getName();
            entry.value=String(kind.getGemId())
            entry.children = kindToComponents.get(kind).sort((l,r) => l.getName().localeCompare(r.getName())).map(comp =>
                {return {
                  name: comp.getName(),
                  value: String(comp.getGemId())
                }}
            )
            entries.push(entry)
          }
      )
    }

    const newHash = JSON.stringify(entries);
    if (newHash !== this.treeComponentItems.hash) {
      this.treeComponentItems.value = entries;
      this.treeComponentItems.hash = newHash;
    }
    return this.treeComponentItems.value
  }

  public override navigateToConnection() : (arg) => void  {
    return (event) => {
      console.warn(event)
      this._router.navigate(["gui/connectionkind/JoinConnectionKindEditPage/"+event.value])
    }
  }

  public override getConnectionsItems(): GemTreeviewEntry[] {
    if(!this.getPack()) {
      return []
    } if(this.lastPack != this.getPack()) {
      this.packService.updateBatteryPack(this.getPack())
    }

    const connectionEntries = []

    const nameToConnections : Map<string, string[]> = new Map()

    this.getPack().getAllConnectionsList().forEach(connKind => {
        const name = connKind.getNameAfterLoad()
        const id = connKind.getGemId()+""
        if(nameToConnections.has(name)) {
          nameToConnections.get(name).push(id)
        } else {
          nameToConnections.set(name, [id])
        }
      })

    for (const [name, connections] of nameToConnections) {
      if(connections.length === 1) {
        const entry = new GemTreeviewEntry();
        entry.name=name
        entry.value=connections[0]
        connectionEntries.push(entry);
      } else {

        const topLevelEntry = new GemTreeviewEntry();
        topLevelEntry.name=connections.length+
            "x "+name
        topLevelEntry.value=connections[0]
        topLevelEntry.children = []

        connections.forEach(c => {
          const secondLevelEntry = new GemTreeviewEntry();
          secondLevelEntry.name = c
          secondLevelEntry.value = c
          topLevelEntry.children.push(secondLevelEntry)
        })

        connectionEntries.push(topLevelEntry);
      }
    }

    const newHash = JSON.stringify(connectionEntries);
    if (newHash !== this.treeConnectionItems.hash) {
      this.treeConnectionItems.value = connectionEntries;
      this.treeConnectionItems.hash = newHash;
    }

    return this.treeConnectionItems.value
  }

  public override navigateToPackEdit() : (arg) =>  void {
    return $ => this._router.navigate(["gui/pack/PackEditPage/"+this.getPack().getGemId()])
  }
}


