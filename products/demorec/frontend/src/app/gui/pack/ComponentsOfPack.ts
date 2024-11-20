/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';
import {StringifyForBuilder} from '@umlp/commonj2ts'
import { ComponentKind } from '@src/demorec/ComponentKind';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ComponentsOfPackComponent ,config} from '@src/gui/pack/ComponentsOfPackComponent';
import {GemTreeviewEntry, GemTreeviewEntryBuilder} from '@umlp/basic';

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
      if(entry.getChildren()) {
        this._router.navigate(["gui/kind/EditComponentKindPage/"+this.getPack().getGemId()+"/"+entry.getValue()])
      } else if(entry.getValue() != unroutable) {
        this._router.navigate(["gui/component/ComponentEditPage/"+entry.getValue()])
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
            entry.setName(kind.getName());
            entry.setValue(String(kind.getGemId()))
            entry.setChildrenList(kindToComponents.get(kind).sort((l,r) => l.getName().localeCompare(r.getName())).map(comp =>
                {return new GemTreeviewEntryBuilder()
                    .name(comp.getName())
                    .value(String(comp.getGemId()))
                    .build();
                }
            ))
            entries.push(entry)
          }
      )
    }

    const newHash = StringifyForBuilder(entries);
    if (newHash !== this.treeComponentItems.hash) {
      this.treeComponentItems.value = entries;
      this.treeComponentItems.hash = newHash;
    }
    return this.treeComponentItems.value
  }

  public override navigateToConnection() : (arg) => void  {
    return (event) => {
      console.warn(event)
      this._router.navigate(["gui/connectionkind/JoinConnectionKindEditPage/"+event.getValue()])
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
        entry.setName(name);
        entry.setValue(connections[0]);
        connectionEntries.push(entry);
      } else {

        const topLevelEntry = new GemTreeviewEntry();
        topLevelEntry.setName(connections.length + "x "+name)
        topLevelEntry.setValue(connections[0])
        topLevelEntry.setChildrenList([]);

        connections.forEach(c => {
          const secondLevelEntry = new GemTreeviewEntry();
          secondLevelEntry.setName(c);
          secondLevelEntry.setValue(c);
          topLevelEntry.addChildren(secondLevelEntry)
        })

        connectionEntries.push(topLevelEntry);
      }
    }

    const newHash = StringifyForBuilder(connectionEntries);
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


