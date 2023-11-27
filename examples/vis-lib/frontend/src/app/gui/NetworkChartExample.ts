/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from '@src/domain/Person';
import { NetworkChartExampleComponent } from '@src/gui/NetworkChartExampleComponent';
import { NetworkData, NetworkEdge, NetworkNode } from '@umlp/charts';

@Component({
  selector: 'network-chart-example',
  templateUrl: './NetworkChartExample.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class NetworkChartExample extends NetworkChartExampleComponent {

  private networkData: NetworkData;
  private networkDataHash: string;

  public getGemNetworkGraphData(): NetworkData {
    if (!this.getP()) {
      return null;
    }
    let person: Person = this.getP();

    let allPersons: Set<Person> = new Set();
    const edges: NetworkEdge[] = this.fillFriendsEdgesRecursively(person, allPersons);
    let nodes: NetworkNode[] = []

    allPersons.forEach(p => nodes.push({ id: p.getName(), label: p.getName(), mass: 50, color: "yellow" }))


    const newNetworkData = { nodes: nodes, edges: edges };

    if (JSON.stringify(newNetworkData) === this.networkDataHash && this.networkData) {
      return this.networkData
    }
    this.networkData = newNetworkData
    this.networkDataHash = JSON.stringify(this.networkData)
    return this.networkData;
  }

  private fillFriendsEdgesRecursively(curPerson: Person, alreadyCaptured: Set<Person>): NetworkEdge[] {
    let edges: NetworkEdge[] = []
    if (alreadyCaptured.has(curPerson)) {
      return edges
    }
    alreadyCaptured.add(curPerson)
    curPerson.getFriends().forEach(p => {
      edges.push({ from: curPerson.getName(), to: p.getName() });
      edges = edges.concat(this.fillFriendsEdgesRecursively(p, alreadyCaptured));
    }
    )
    return edges;
  }
}


