
import { GemTreeviewExampleComponentTOP } from '@src/example_basic/GemTreeviewExampleComponentTOP';
import { Component } from '@angular/core';
import { GemTreeviewEntry } from '@umlp/basic';
@Component({
  selector: 'GemTreeviewExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_basic/GemTreeviewExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemTreeviewExampleComponent extends GemTreeviewExampleComponentTOP  {

  getItem (): Array<GemTreeviewEntry>{

      // Sample data for the tree view
      const treeData: Array<GemTreeviewEntry> = [
          {
              expanded: true, // or false if you want it to be initially collapsed
              name: "Item 1",
              children: [
                  {
                      expanded: true,
                      name: "Item 1.1",
                      children: [] // No children for this node
                  },
                  {
                      expanded: false,
                      name: "Item 1.2",
                      children: [
                          // Further nested children, if any
                          {
                              expanded: true,
                              name: "Item 1.2.1",
                              children: [] // Example of further nesting
                          }
                      ]
                  }
              ]
          },
          {
              expanded: false,
              name: "Item 2",
              children: [] // Assuming no children for this node
          }
          // ... more items if necessary
      ];

      return treeData;
  }




}

