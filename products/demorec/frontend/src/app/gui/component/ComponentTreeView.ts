/* (c) https://github.com/MontiCore/monticore */

import { GemTreeviewEntry } from '@umlp/basic';
import { DemoRecComponent } from '@src/demorec/DemoRecComponent';


import { ComponentTreeViewComponent,config } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentTreeViewComponent';

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
export  class ComponentTreeView extends ComponentTreeViewComponent {

  treeViewDataCheck = { value: null, hash: null, calculating: false };

  seenCompsRecursionState : Set<number> = new Set<number>()

  public override getHierarchicalComponentsItems (): Array<GemTreeviewEntry> {
    console.debug("Create Hierarchical Component")
    if(!(this.getComponent())) {
      return [];
    }
    this.seenCompsRecursionState = new Set()
    const treeViewEntries = this.createTreeRecursively(this.getComponent())

    const newHash = JSON.stringify(treeViewEntries);
    if(this.treeViewDataCheck.hash !== newHash) {
      this.treeViewDataCheck.hash = newHash;
      this.treeViewDataCheck.value = treeViewEntries;
    }
    return this.treeViewDataCheck.value
  }

  private createTreeRecursively(curComponent : DemoRecComponent): GemTreeviewEntry[] {
    const children : GemTreeviewEntry[] = []

    if(this.seenCompsRecursionState.has(curComponent.getGemId())) {
      return children
    }
    this.seenCompsRecursionState.add(curComponent.getGemId())

    const alreadySeenChildren = new Set()
    curComponent.getComposedOf().forEach(joinConnection => {
      joinConnection.getConnects().forEach(child => {
        if(!alreadySeenChildren.has(child.getGemId())) {
          alreadySeenChildren.add(child.getGemId())
          let descr = ":"+child.getGemId()
          if(child.getName()) {
            descr = child.getName()+descr
          }
          children.push(new GemTreeviewEntry(false,
              descr, this.createTreeRecursively(child)))
        }

      })
    })
    return children
  }
}


