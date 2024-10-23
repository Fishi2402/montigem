/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGraphChartExampleComponent, config } from '@src/example_charts/GemGraphChartExampleComponent';
import {
  GemNetworkData, GemNetworkDataBuilder,
  GemNetworkEdge,
  GemNetworkEdgeBuilder,
  GemNetworkNode,
  GemNetworkNodeBuilder
} from '@umlp/charts';
import { Person } from '@src/domain/Person';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemGraphChartExample extends GemGraphChartExampleComponent {
  private chartData: GemNetworkData;
  private chartDataHash: string;

  public override getNetworkgraphData(): GemNetworkData {
    if (!this.persons) {
      return null;
    }
    let nodes: GemNetworkNode[] = [];
    let edges: GemNetworkEdge[] = [];
    let nodeId = 1;
    let mapOfPersons = new Map<Person, number>;
    let mapOfriends = new Map<number, Person[]>;

    for (const person of this.persons) {
      let node = new GemNetworkNodeBuilder()
          .id(`${nodeId}`)
          .color("Red")
          .label(person.getName())
          .mass(person.getFriendsList().length)
          .build();
      mapOfriends.set(nodeId, person.getFriendsList());
      mapOfPersons.set(person, nodeId);
      nodeId++;
      nodes.push(node);
    }

    mapOfriends.forEach((friends, nodeId) => {
      for (let friend of friends) {
        let edge = new GemNetworkEdgeBuilder()
            .from(`${nodeId}`)
            .to(`${mapOfPersons.get(friend)}`)
            .length(200)
            .build();
        edges.push(edge);
      }
    })
    let data = new GemNetworkDataBuilder()
        .nodes(nodes)
        .edges(edges)
        .build();

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }

}
