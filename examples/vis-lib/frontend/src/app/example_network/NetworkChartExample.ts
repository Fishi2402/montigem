/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { NetworkChartExampleComponent ,config} from 'example_network/NetworkChartExampleComponent';
import {Graph, GraphBuilder, GraphEdge, GraphEdgeBuilder, GraphNode, GraphNodeBuilder} from '@umlp/networkgraph';
import { GemGraph } from '@umlp/networkgraph';

@Component({
  standalone:true,
  imports:[...config.imports,
    GemGraph
  ],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class NetworkChartExample extends NetworkChartExampleComponent {

  boundingBox :{x:[number,number], y:[number,number] };

  public override getPersonGraphCustomBoundingBox() :{x:[number,number], y:[number,number] } {
    if(this.boundingBox) {
      console.debug("Returning bounding box:", this.boundingBox)
      return this.boundingBox
    }
    return {
      x:[-1,1],
      y:[-1,1]
    }
  }

  protected nodes: GraphNode[] = [
    new GraphNodeBuilder().id("1").label("Node 1").group("Cluster A").size(100).nodeProgram("triangle").build(),

    new GraphNodeBuilder().id("12").label("Node 1").group("Cluster A").size(100).nodeProgram("square").build(),
    new GraphNodeBuilder().id("13").label("Node ").group("Cluster A").size(100).nodeProgram("circle").build(),

    new GraphNodeBuilder().id("2").label("Node 3").group("Cluster A").size(35).build(),
    new GraphNodeBuilder().id("3").label("Node 4").group("Cluster A").size(44).build(),
    new GraphNodeBuilder().id("4").label("Node 5").group("Cluster A").size(55).hoverLabel( "another hover label!").color("#ffc0cb").build(),

    new GraphNodeBuilder().id("5").label("Node 6").group("Cluster A").size(54).fixed(true).x(0).y(0).tooltip("custom Tooltip!").build(),
    new GraphNodeBuilder().id("6").label("Node 7").group("Cluster B").build(),
    new GraphNodeBuilder().id("7").label("Node 8").group("Cluster B").build(),
    new GraphNodeBuilder().id("8").label("Node 9").group("Cluster B").build(),
    new GraphNodeBuilder().id("9").label("Node 10").group("Cluster B").build(),
    new GraphNodeBuilder().id("10").label("Node 11").group("Cluster B").build(),
    new GraphNodeBuilder().id("11").label("Node 12").hover("No Group Demo!").build()
  ]

  protected edges: GraphEdge[] = [
      new GraphEdgeBuilder().id("1").source("1").target("2").size(25).directed(true).color("#fc4c4e").name("Edge 1-2").build(),
      new GraphEdgeBuilder().id("2").source("2").target("3").name("Edge 2-3").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("3").source("3").target("4").name("Edge 3-4").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("4").source("4").target("5").name("Edge 4-5").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("5").source("1").target("5").name("Edge 1-5").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("6").source("2").target("4").name("Edge 2-4").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("7").source("6").target("7").name("Edge 6-7").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("8").source("7").target("8").name("Edge 7-8").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("9").source("8").target("9").name("Edge 8-9").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("10").source("9").target("10").name("Edge 9-10").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("11").source("6").target("10").name("Edge 6-10").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("12").source("7").target("9").name("Edge 7-9").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("13").source("1").target("6").name("Edge 1-6").color("#fc4c4e").build(),
      new GraphEdgeBuilder().id("14").source("5").target("10").name("Edge 5-10").build(),
      new GraphEdgeBuilder().id("15").source("3").target("8").name("Edge 3-8").build(),
      new GraphEdgeBuilder().id("16").source("1").target("3").name("Edge 1-3").build(),
      new GraphEdgeBuilder().id("17").source("2").target("5").name("Edge 2-5").build(),
      new GraphEdgeBuilder().id("18").source("4").target("1").name("Edge 4-1").build(),
      new GraphEdgeBuilder().id("19").source("6").target("8").name("Edge 6-8").build(),
      new GraphEdgeBuilder().id("20").source("7").target("10").name("Edge 7-10").build(),
      new GraphEdgeBuilder().id("21").source("9").target("6").name("Edge 9-6").build()
  ]

  public getPersonGraphEdges (): Array<GraphEdge> {
    return this.edges
  }

  public getPersonGraphNodes (): Array<GraphNode> {
    return this.nodes
  }

  public getPersonGraphGraph (): Graph {
    return new GraphBuilder()
    .edges(this.getPersonGraphEdges())
    .nodes(this.getPersonGraphNodes())
    .build();
  }

  public override getPositions() {
    return (nodePos) => {
      const xMin = nodePos.map(n => n.getX()).reduce((a, b)=>Math.min(a, b));
      const xMax = nodePos.map(n => n.getX()).reduce((a, b)=>Math.max(a, b));

      const yMin = nodePos.map(n => n.getY()).reduce((a, b)=>Math.min(a, b));
      const yMax = nodePos.map(n => n.getY()).reduce((a, b)=>Math.max(a, b));

      const newBoundingBox :  {x:[number,number], y:[number,number] } = {
        x:[xMin, xMax],
        y:[yMin, yMax]
      }

      if(!this.boundingBox) {
        console.debug("New bounding box:", newBoundingBox)
        console.debug("Bounding box:", this.getPersonGraphCustomBoundingBox())
        //this.boundingBox = newBoundingBox
      }
    }
  }


}


