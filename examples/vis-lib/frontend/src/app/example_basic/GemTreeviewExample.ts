/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from '@src/domain/Person';
import { GemTreeviewExampleComponent, config } from '@src/example_basic/GemTreeviewExampleComponent';
import { GemTreeviewEntry } from '@umlp/basic';
import { Event } from '@umlp/common';

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

      entry.name = p.getName();
      entry.children = [];
      for (const f of p.getFriendsList()) {
        const friend = new GemTreeviewEntry();
        friend.name = f.getName();
        entry.children.push(friend);
      }
    }
    const newHash = JSON.stringify(result);
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
