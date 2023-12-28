/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGraphChartExampleComponent,config } from '@src/example_charts/GemGraphChartExampleComponent';
import { GemNetworkData, GemNetworkEdge, GemNetworkNode } from '@umlp/charts';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemGraphChartExample extends GemGraphChartExampleComponent {
  nodes: GemNetworkNode[] = [
    {
      id: "1",
      color: "Red"
    },
    {
      id: "2",
      color: "Red"
    },
    {
      id: "3",
      color: "Red"
    },
    {
      id: "4",
      color: "Green"
    },
    {
      id: "5",
      color: "Green"
    },
    {
      id: "6",
      color: "Green"
    },
  ];
  edges: GemNetworkEdge[] = [
    {
      from: "1",
      to: "2",
      length: 250
    },
    {
      from: "1",
      to: "3",
      length: 250
    },
    {
      from: "4",
      to: "5",
      length: 250
    },
    {
      from: "5",
      to: "6",
      length: 250
    },
    {
      from: "6",
      to: "4",
      length: 250
    },
  ];

  data: GemNetworkData = {
    nodes: this.nodes,
    edges: this.edges
  }

}
