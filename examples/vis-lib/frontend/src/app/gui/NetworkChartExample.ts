/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { Person } from '@src/domain/Person';
import { NetworkChartExampleComponent ,config} from '@src/gui/NetworkChartExampleComponent';
import { GraphEdge, GraphNode } from '@umlp/networkgraph';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class NetworkChartExample extends NetworkChartExampleComponent {

  private gNodes : GraphNode[];
  private gEdges : GraphEdge[];
  private gNodesDataHash: string;
  private gEdgesDataHash: string;

  public override getPersonGraphEdges() : GraphEdge[] {
    if (!this.getP()) {
      return null;
    }
    let person: Person = this.getP();

    let allPersons: Set<Person> = new Set();
    const edges: GraphEdge[] = this.fillFriendsEdgesRecursively(person, allPersons);
    if (JSON.stringify(this.gEdges) === this.gEdgesDataHash && this.gEdges) {
      return this.gEdges
    }
    this.gEdges = edges
    this.gEdgesDataHash = JSON.stringify(this.gEdges)
    return this.gEdges;

  }

  public override getPersonGraphNodes(): GraphNode[] {
    if (!this.getP()) {
      return null;
    }
    let person: Person = this.getP();
    let allPersons: Set<Person> = new Set();
    this.fillFriendsEdgesRecursively(person, allPersons);
    let nodes: GraphNode[] = []

    allPersons.forEach(p => nodes.push({ id: p.getName(), label: p.getName(), group: p.constructor.name, mass: 50, color: "yellow" }))

    if (JSON.stringify(this.gNodes) === this.gNodesDataHash && this.gNodes) {
      return this.gNodes
    }
    this.gNodes = nodes
    this.gNodesDataHash = JSON.stringify(this.gNodes)
    return this.gNodes;
  }

  private fillFriendsEdgesRecursively(curPerson: Person, alreadyCaptured: Set<Person>): GraphEdge[] {
    let edges: GraphEdge[] = []
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


