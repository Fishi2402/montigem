
import { GemTreeviewExampleComponentTOP } from '@src/example_basic/GemTreeviewExampleComponentTOP';
import { Component } from '@angular/core';
import { GemTreeviewEntry } from '@umlp/basic';
@Component({
  selector: 'GemTreeviewExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_basic/GemTreeviewExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemTreeviewExampleComponent extends GemTreeviewExampleComponentTOP  {
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

  getItem(): Array<GemTreeviewEntry>{
      return this.treeData;
  }

  gemTreeview0Expand(event: any): void{
   let itemName = event.itemName;
   this.expandTreeItem(this.treeData, itemName);
  }

  expandTreeItem(items: Array<GemTreeviewEntry>, itemName: string): void {
  for(let item of items){
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

