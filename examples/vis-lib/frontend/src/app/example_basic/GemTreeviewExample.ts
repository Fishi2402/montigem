/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from 'domain/Person';
import { GemTreeviewExampleComponent, config } from 'example_basic/GemTreeviewExampleComponent';
import { GemTreeviewEntry } from '@umlp/basic';
import { Event } from '@umlp/common';
import {stringifyWithoutRealBuilder} from "StringifyWithoutRealBuilder";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemTreeviewExample extends GemTreeviewExampleComponent {

  protected treeItems: { value: GemTreeviewEntry[], hash: string }
    = { value: [], hash: '' };

  public override getTreeItems(): GemTreeviewEntry[] {
    const persons: Person[] = this.getPersons() || [];
    const result: GemTreeviewEntry[] = [];

    for (const p of persons) {
      const entry = new GemTreeviewEntry();
      result.push(entry);

      entry.setName(p.getName());
      entry.setChildrenList([]);
      for (const f of p.getFriendsList()) {
        const friend = new GemTreeviewEntry();
        friend.setName(f.getName());
        entry.addChildren(friend);
      }
    }
    const newHash = stringifyWithoutRealBuilder(result);
    if (newHash !== this.treeItems.hash) {
      this.treeItems.value = result;
      this.treeItems.hash = newHash;
    }

    return this.treeItems.value;
  }


  public override sayHello(): Event<GemTreeviewEntry> {
    return () => alert("Hello");
  }

}
