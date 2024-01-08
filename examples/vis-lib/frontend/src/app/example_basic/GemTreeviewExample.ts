/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemTreeviewExampleComponent,config } from '@src/example_basic/GemTreeviewExampleComponent';
import { GemTreeviewEntry } from '@umlp/basic';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemTreeviewExample extends GemTreeviewExampleComponent {
  treeData: Array<GemTreeviewEntry> = [
    {
      expanded: true,
      name: "Item 1",
      children: [
        {
          expanded: true,
          name: "Item 1.1",
          children: []
        },
        {
          expanded: false,
          name: "Item 1.2",
          children: [
            {
              expanded: true,
              name: "Item 1.2.1",
              children: []
            }
          ]
        }
      ]
    },
    {
      expanded: false,
      name: "Item 2",
      children: []
    }
  ];

  getItem(): Array<GemTreeviewEntry> {
    return this.treeData;
  }

  gemTreeview0Expand(event: any): void {
    let itemName = event.itemName;
    this.expandTreeItem(this.treeData, itemName);
  }

  expandTreeItem(items: Array<GemTreeviewEntry>, itemName: string): void {
    for (let item of items) {
      if (item.name === itemName) {
        item.expanded = true;
        return;
      }
      if (item.children && item.children.length > 0) {
        this.expandTreeItem(item.children, itemName);
      }
    }
  }
}
